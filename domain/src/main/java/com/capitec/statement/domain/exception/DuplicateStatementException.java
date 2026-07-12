package com.capitec.statement.domain.exception;

public class DuplicateStatementException extends RuntimeException {
    public DuplicateStatementException(String message) {
        super(message);
    }
}
