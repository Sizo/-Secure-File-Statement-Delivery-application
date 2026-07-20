package com.capitec.statement.api.controller;

import com.capitec.statement.api.service.StatementService;
import com.capitec.statement.domain.entity.Statement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/statements")
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping
    public ResponseEntity<List<StatementResponse>> getStatements(@RequestAttribute("customerId") String customerId) {
        List<Statement> statements = statementService.getStatementsForCustomer(customerId);
        List<StatementResponse> response = statements.stream()
                .map(s -> new StatementResponse(s.getId(), s.getStatementMonth(), s.getStatementYear(), s.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{statementId}/download")
    public ResponseEntity<DownloadUrlResponse> downloadStatement(
            @PathVariable UUID statementId,
            @RequestAttribute("customerId") String customerId,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        String downloadUrl = statementService.getStatementForDownload(statementId, customerId, ipAddress, userAgent);
        return ResponseEntity.ok(new DownloadUrlResponse(downloadUrl));
    }

    public record StatementResponse(UUID id, int month, int year, java.time.LocalDateTime createdAt) {}
    public record DownloadUrlResponse(String url) {}
}
