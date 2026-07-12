package com.capitec.statement.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DownloadUrlResponse(
        UUID statementId,
        String downloadUrl,
        long expiresInSeconds,
        OffsetDateTime generatedAt
) {
}
