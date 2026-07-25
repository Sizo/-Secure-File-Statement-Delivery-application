package com.capitec.statement.domain.exception;

public class CustomerIdentityException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    public CustomerIdentityException(String message) {
        super(message);
    }
}
