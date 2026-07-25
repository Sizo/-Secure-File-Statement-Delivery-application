package com.capitec.statement.domain.exception;

public class DuplicateStatementException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    public DuplicateStatementException(String message) {
        super(message);
    }
}
