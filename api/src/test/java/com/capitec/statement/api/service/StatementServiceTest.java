package com.capitec.statement.api.service;

import com.capitec.statement.api.exception.AccessDeniedException;
import com.capitec.statement.api.exception.StatementNotFoundException;
import com.capitec.statement.domain.entity.AuditStatus;
import com.capitec.statement.domain.entity.Statement;
import com.capitec.statement.domain.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private StatementService statementService;

    private Statement statement;
    private UUID statementId;
    
    @BeforeEach
    void setUp() {
        statementId = UUID.randomUUID();
        statement = new Statement();
        statement.setId(statementId);
        statement.setCustomerId("cust-123");
        statement.setS3ObjectKey("statements/cust-123/stmt.pdf");
    }

    @Test
    void getStatementsForCustomer_ReturnsList() {
        when(statementRepository.findByCustomerIdOrderByStatementYearDescStatementMonthDesc("cust-123"))
                .thenReturn(List.of(statement));
                
        List<Statement> result = statementService.getStatementsForCustomer("cust-123");
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo("cust-123");
    }

    @Test
    void getStatementForDownload_ValidOwnership_ReturnsUrl() {
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));
        when(s3StorageService.generatePresignedUrl(statement.getS3ObjectKey())).thenReturn("https://s3.url");
        
        String url = statementService.getStatementForDownload(statementId, "cust-123", "127.0.0.1", "curl");
        
        assertThat(url).isEqualTo("https://s3.url");
        verify(auditService).logDownloadAttempt(statementId, "cust-123", "127.0.0.1", "curl", AuditStatus.SUCCESS, null);
    }

    @Test
    void getStatementForDownload_IdorViolation_ThrowsAccessDeniedException() {
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));
        
        assertThatThrownBy(() -> statementService.getStatementForDownload(statementId, "hacker-999", "127.0.0.1", "curl"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission");
                
        verify(auditService).logDownloadAttempt(eq(statementId), eq("hacker-999"), eq("127.0.0.1"), eq("curl"), eq(AuditStatus.DENIED), any());
    }

    @Test
    void getStatementForDownload_MissingStatement_ThrowsStatementNotFoundException() {
        when(statementRepository.findById(statementId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> statementService.getStatementForDownload(statementId, "cust-123", "127.0.0.1", "curl"))
                .isInstanceOf(StatementNotFoundException.class);
                
        verify(auditService).logDownloadAttempt(eq(statementId), eq("cust-123"), eq("127.0.0.1"), eq("curl"), eq(AuditStatus.DENIED), any());
    }
}
