package com.capitec.statement.worker.listener;

import com.capitec.statement.domain.dto.StatementBatchMessage;
import com.capitec.statement.worker.service.StatementProcessingService;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementBatchListenerTest {

    @Mock
    private StatementProcessingService statementProcessingService;

    @Mock
    private Acknowledgement acknowledgement;

    @InjectMocks
    private StatementBatchListener statementBatchListener;

    private StatementBatchMessage message;

    @BeforeEach
    void setUp() {
        message = new StatementBatchMessage("cust-123", "acc-456", "2023-01");
    }

    @Test
    void testReceiveMessage_Success() {
        statementBatchListener.receiveMessage(message, acknowledgement);
        verify(statementProcessingService, times(1)).processStatement(message);
        verify(acknowledgement, times(1)).acknowledge();
    }

    @Test
    void testReceiveMessage_Failure_DoesNotAcknowledge() {
        doThrow(new RuntimeException("DB Error")).when(statementProcessingService).processStatement(message);
        assertThrows(RuntimeException.class, () -> statementBatchListener.receiveMessage(message, acknowledgement));
        verify(statementProcessingService, times(1)).processStatement(message);
        verify(acknowledgement, never()).acknowledge();
    }
}