package com.capitec.statement.api.service;

import com.capitec.statement.api.exception.AccessDeniedException;
import com.capitec.statement.api.exception.StatementNotFoundException;
import com.capitec.statement.domain.entity.AuditStatus;
import com.capitec.statement.domain.entity.Statement;
import com.capitec.statement.domain.repository.StatementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StatementService {

    private final StatementRepository statementRepository;
    private final S3StorageService s3StorageService;
    private final AuditService auditService;

    public StatementService(StatementRepository statementRepository, 
                            S3StorageService s3StorageService,
                            AuditService auditService) {
        this.statementRepository = statementRepository;
        this.s3StorageService = s3StorageService;
        this.auditService = auditService;
    }

    public List<Statement> getStatementsForCustomer(String customerId) {
        return statementRepository.findByCustomerIdOrderByStatementYearDescStatementMonthDesc(customerId);
    }

    public String getStatementForDownload(UUID statementId, String customerId, String ipAddress, String userAgent) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> {
                    auditService.logDownloadAttempt(statementId, customerId, ipAddress, userAgent, AuditStatus.DENIED, "Statement not found");
                    return new StatementNotFoundException("Statement not found with ID: " + statementId);
                });

        if (!statement.getCustomerId().equals(customerId)) {
            auditService.logDownloadAttempt(statementId, customerId, ipAddress, userAgent, AuditStatus.DENIED, "IDOR violation attempt");
            throw new AccessDeniedException("You do not have permission to access this statement");
        }

        String downloadUrl = s3StorageService.generatePresignedUrl(statement.getS3ObjectKey());
        
        auditService.logDownloadAttempt(statementId, customerId, ipAddress, userAgent, AuditStatus.SUCCESS, null);
        
        return downloadUrl;
    }
}
