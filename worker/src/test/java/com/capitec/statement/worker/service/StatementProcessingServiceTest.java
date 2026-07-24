package com.capitec.statement.worker.service;

import com.capitec.statement.domain.dto.StatementBatchMessage;
import com.capitec.statement.domain.entity.Statement;
import com.capitec.statement.domain.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementProcessingServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @Mock
    private S3UploadService s3UploadService;

    @InjectMocks
    private StatementProcessingService statementProcessingService;

    private StatementBatchMessage message;

    @BeforeEach
    void setUp() {
        message = new StatementBatchMessage("cust-1", "acc-1", "2023-01", new byte[]{1, 2, 3});
    }

    @Test
    void processStatement_HappyPath() {
        when(statementRepository.existsByCustomerIdAndAccountNumberAndStatementPeriod("cust-1", "acc-1", "2023-01"))
                .thenReturn(false);
        byte[] pdfBytes = "mock-pdf".getBytes();
        when(pdfGeneratorService.generatePdf("cust-1", "acc-1", "2023-01")).thenReturn(pdfBytes);
        when(s3UploadService.generateS3Key(eq("cust-1"), eq("2023-01"), any())).thenReturn("s3-key");
        
        Statement saved = new Statement();
        when(statementRepository.save(any(Statement.class))).thenReturn(saved);

        Statement result = statementProcessingService.processStatement(message);

        assertNotNull(result);
        verify(s3UploadService).uploadStatement("s3-key", pdfBytes);
        
        ArgumentCaptor<Statement> captor = ArgumentCaptor.forClass(Statement.class);
        verify(statementRepository).save(captor.capture());
        assertEquals("cust-1", captor.getValue().getCustomerId());
        assertEquals("s3-key", captor.getValue().getS3Key());
    }

    @Test
    void processStatement_Duplicate() {
        when(statementRepository.existsByCustomerIdAndAccountNumberAndStatementPeriod("cust-1", "acc-1", "2023-01"))
                .thenReturn(true);

        Statement result = statementProcessingService.processStatement(message);

        assertNull(result);
        verifyNoInteractions(pdfGeneratorService, s3UploadService);
        verify(statementRepository, never()).save(any());
    }
    
    @Test
    void processStatement_S3UploadFailure_RollsBackTransaction() {
        when(statementRepository.existsByCustomerIdAndAccountNumberAndStatementPeriod("cust-1", "acc-1", "2023-01"))
                .thenReturn(false);
        byte[] pdfBytes = "mock-pdf".getBytes();
        when(pdfGeneratorService.generatePdf("cust-1", "acc-1", "2023-01")).thenReturn(pdfBytes);
        when(s3UploadService.generateS3Key(eq("cust-1"), eq("2023-01"), any())).thenReturn("s3-key");
        
        doThrow(new RuntimeException("S3 Down")).when(s3UploadService).uploadStatement("s3-key", pdfBytes);
        
        assertThrows(RuntimeException.class, () -> statementProcessingService.processStatement(message));
        
        verify(statementRepository, never()).save(any());
    }
}