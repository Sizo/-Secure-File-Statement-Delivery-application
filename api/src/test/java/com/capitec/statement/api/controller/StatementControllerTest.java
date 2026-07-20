package com.capitec.statement.api.controller;

import com.capitec.statement.api.exception.AccessDeniedException;
import com.capitec.statement.api.exception.StatementNotFoundException;
import com.capitec.statement.api.filter.CustomerIdentityFilter;
import com.capitec.statement.api.filter.JwtUtil;
import com.capitec.statement.api.service.StatementService;
import com.capitec.statement.domain.entity.Statement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatementController.class)
@Import({CustomerIdentityFilter.class, JwtUtil.class})
class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatementService statementService;

    @Test
    void getStatements_Returns200_WithStatements() throws Exception {
        Statement stmt = new Statement();
        stmt.setId(UUID.randomUUID());
        stmt.setStatementMonth(7);
        stmt.setStatementYear(2026);
        stmt.setCreatedAt(LocalDateTime.now());
        
        when(statementService.getStatementsForCustomer("cust-123"))
                .thenReturn(List.of(stmt));

        mockMvc.perform(get("/api/v1/statements")
                .header("X-Customer-ID", "cust-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(stmt.getId().toString()))
                .andExpect(jsonPath("$[0].month").value(7))
                .andExpect(jsonPath("$[0].year").value(2026));
    }

    @Test
    void downloadStatement_ReturnsPresignedUrl() throws Exception {
        UUID stmtId = UUID.randomUUID();
        when(statementService.getStatementForDownload(eq(stmtId), eq("cust-123"), any(), any()))
                .thenReturn("https://s3.url");

        mockMvc.perform(get("/api/v1/statements/{id}/download", stmtId)
                .header("X-Customer-ID", "cust-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://s3.url"));
    }

    @Test
    void downloadStatement_IdorViolation_Returns403() throws Exception {
        UUID stmtId = UUID.randomUUID();
        when(statementService.getStatementForDownload(eq(stmtId), eq("hacker-999"), any(), any()))
                .thenThrow(new AccessDeniedException("Access Denied"));

        mockMvc.perform(get("/api/v1/statements/{id}/download", stmtId)
                .header("X-Customer-ID", "hacker-999"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Access Denied"))
                .andExpect(jsonPath("$.title").value("Access Denied"));
    }

    @Test
    void missingIdentity_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/statements"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
}
