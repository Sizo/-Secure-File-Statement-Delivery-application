package com.capitec.statement.api.exception;

public class StatementNotFoundException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    public StatementNotFoundException(String message) {
        super(message);
    }
}
