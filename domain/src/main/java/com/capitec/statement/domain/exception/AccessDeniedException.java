package com.capitec.statement.domain.exception;

import java.util.UUID;

public class AccessDeniedException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    private final UUID statementId;
    private final String customerId;

    public AccessDeniedException(UUID statementId, String customerId) {
        super("Access denied for customer: " + customerId + " on statement: " + statementId);
        this.statementId = statementId;
        this.customerId = customerId;
    }

    public UUID getStatementId() {
        return statementId;
    }

    public String getCustomerId() {
        return customerId;
    }
}
