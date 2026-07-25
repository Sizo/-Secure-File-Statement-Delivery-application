package com.capitec.statement.api.exception;

public class AccessDeniedException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    public AccessDeniedException(String message) {
        super(message);
    }
}
