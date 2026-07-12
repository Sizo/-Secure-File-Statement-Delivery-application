package com.capitec.statement.domain.dto;

public record StatementBatchMessage(
        String customerId,
        String accountNumber,
        String statementPeriod,
        byte[] statementData
) {
}
