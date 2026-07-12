package com.capitec.statement.domain.dto;

import com.capitec.statement.domain.entity.Statement;
import java.time.OffsetDateTime;
import java.util.UUID;

public record StatementResponse(
        UUID statementId,
        String accountNumber,
        String statementPeriod,
        Long fileSizeBytes,
        String contentType,
        OffsetDateTime createdAt
) {
    public static StatementResponse fromEntity(Statement statement) {
        return new StatementResponse(
                statement.getStatementId(),
                statement.getAccountNumber(),
                statement.getStatementPeriod(),
                statement.getFileSizeBytes(),
                statement.getContentType(),
                statement.getCreatedAt()
        );
    }
}
