package com.capitec.statement.worker.listener;

import com.capitec.statement.domain.dto.StatementBatchMessage;
import com.capitec.statement.worker.service.StatementProcessingService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StatementBatchListener {

    private static final Logger log = LoggerFactory.getLogger(StatementBatchListener.class);
    private final StatementProcessingService statementProcessingService;

    public StatementBatchListener(StatementProcessingService statementProcessingService) {
        this.statementProcessingService = statementProcessingService;
    }

    @SqsListener(value = "${app.sqs.queue-name}", acknowledgementMode = "MANUAL")
    public void receiveMessage(StatementBatchMessage message, Acknowledgement acknowledgement) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("customerId", message.customerId());
        MDC.put("accountNumber", message.accountNumber());
        MDC.put("statementPeriod", message.statementPeriod());

        try {
            log.info("Received statement batch message for processing");
            statementProcessingService.processStatement(message);
            log.debug("Acknowledging message after successful processing");
            acknowledgement.acknowledge();
            log.info("Successfully processed and acknowledged statement batch message");
        } catch (Exception e) {
            log.error("Error processing statement batch message. Message will return to queue for retry.", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}