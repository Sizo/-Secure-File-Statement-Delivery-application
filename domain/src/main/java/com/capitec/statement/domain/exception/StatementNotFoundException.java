package com.capitec.statement.domain.exception;

import java.util.UUID;

public class StatementNotFoundException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    private final UUID statementId;

    public StatementNotFoundException(UUID statementId) {
        super("Statement not found for id: " + statementId);
        this.statementId = statementId;
    }

    public UUID getStatementId() {
        return statementId;
    }
}
