package com.capitec.statement.worker.service;

import com.capitec.statement.domain.dto.StatementBatchMessage;
import com.capitec.statement.domain.entity.Statement;
import com.capitec.statement.domain.repository.StatementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StatementProcessingService {

    private static final Logger log = LoggerFactory.getLogger(StatementProcessingService.class);

    private final StatementRepository statementRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final S3UploadService s3UploadService;

    public StatementProcessingService(
            StatementRepository statementRepository,
            PdfGeneratorService pdfGeneratorService,
            S3UploadService s3UploadService) {
        this.statementRepository = statementRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.s3UploadService = s3UploadService;
    }

    @Transactional
    public Statement processStatement(StatementBatchMessage message) {
        String customerId = message.customerId();
        String accountNumber = message.accountNumber();
        String statementPeriod = message.statementPeriod();

        log.debug("Checking for duplicate statement idempotency");
        if (statementRepository.existsByCustomerIdAndAccountNumberAndStatementPeriod(customerId, accountNumber, statementPeriod)) {
            log.info("Statement already exists for customer {}, account {}, period {}. Skipping processing.",
                    customerId, accountNumber, statementPeriod);
            return null;
        }

        log.debug("Generating PDF statement");
        byte[] pdfContent = pdfGeneratorService.generatePdf(customerId, accountNumber, statementPeriod);

        UUID statementId = UUID.randomUUID();
        String s3Key = s3UploadService.generateS3Key(customerId, statementPeriod, statementId);

        log.debug("Uploading PDF statement to S3 with key: {}", s3Key);
        s3UploadService.uploadStatement(s3Key, pdfContent);

        log.debug("Creating and saving Statement entity to DB");
        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setCustomerId(customerId);
        statement.setAccountNumber(accountNumber);
        statement.setStatementPeriod(statementPeriod);
        statement.setS3Key(s3Key);
        statement.setFileSizeBytes((long) pdfContent.length);
        statement.setContentType("application/pdf");
        
        return statementRepository.save(statement);
    }
}