package com.capitec.statement.domain.exception;

import java.util.UUID;

public class StatementNotFoundException extends RuntimeException {
    private final UUID statementId;

    public StatementNotFoundException(UUID statementId) {
        super("Statement not found for id: " + statementId);
        this.statementId = statementId;
    }

    public UUID getStatementId() {
        return statementId;
    }
}
