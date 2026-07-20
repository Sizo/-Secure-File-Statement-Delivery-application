package com.capitec.statement.api.exception;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StatementNotFoundException.class)
    public ProblemDetail handleStatementNotFound(StatementNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("https://api.capitec.co.za/errors/statement-not-found"));
        problemDetail.setTitle("Statement Not Found");
        addCustomProperties(problemDetail);
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setType(URI.create("https://api.capitec.co.za/errors/access-denied"));
        problemDetail.setTitle("Access Denied");
        addCustomProperties(problemDetail);
        return problemDetail;
    }

    @ExceptionHandler(CustomerIdentityException.class)
    public ProblemDetail handleCustomerIdentity(CustomerIdentityException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setType(URI.create("https://api.capitec.co.za/errors/unauthorized"));
        problemDetail.setTitle("Unauthorized");
        addCustomProperties(problemDetail);
        return problemDetail;
    }

    private void addCustomProperties(ProblemDetail problemDetail) {
        problemDetail.setProperty("timestamp", Instant.now().toString());
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        problemDetail.setProperty("correlationId", correlationId);
    }
}
