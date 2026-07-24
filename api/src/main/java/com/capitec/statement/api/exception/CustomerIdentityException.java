package com.capitec.statement.api.exception;

public class CustomerIdentityException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    public CustomerIdentityException(String message) {
        super(message);
    }
    
    public CustomerIdentityException(String message, Throwable cause) {
        super(message, cause);
    }
}
