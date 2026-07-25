package com.capitec.statement.api.controller;

import com.capitec.statement.api.generated.controller.StatementsApi;
import com.capitec.statement.api.generated.dto.DownloadUrlResponse;
import com.capitec.statement.api.generated.dto.StatementResponse;
import com.capitec.statement.api.service.StatementService;
import com.capitec.statement.domain.entity.Statement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class StatementController implements StatementsApi {

    private final StatementService statementService;
    private final HttpServletRequest request;

    @Autowired
    public StatementController(StatementService statementService, HttpServletRequest request) {
        this.statementService = statementService;
        this.request = request;
    }

    @Override
    public ResponseEntity<List<StatementResponse>> getStatements() {
        String customerId = (String) request.getAttribute("customerId");
        List<Statement> statements = statementService.getStatementsForCustomer(customerId);
        
        List<StatementResponse> response = statements.stream()
                .map(s -> {
                    StatementResponse dto = new StatementResponse();
                    dto.setId(s.getStatementId());
                    dto.setAccountId(s.getAccountNumber());
                    dto.setStatementDate(LocalDate.parse(s.getStatementPeriod() + "-01"));
                    dto.setCreatedAt(s.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DownloadUrlResponse> getStatementDownloadUrl(UUID statementId) {
        String customerId = (String) request.getAttribute("customerId");
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        String downloadUrl = statementService.getStatementForDownload(statementId, customerId, ipAddress, userAgent);
        
        DownloadUrlResponse response = new DownloadUrlResponse();
        response.setUrl(URI.create(downloadUrl));
        response.setExpiresAt(OffsetDateTime.now().plusMinutes(15));
        
        return ResponseEntity.ok(response);
    }
}
