package com.capitec.statement.api.service;

import com.capitec.statement.domain.entity.AuditStatus;
import com.capitec.statement.domain.entity.DownloadAuditLog;
import com.capitec.statement.domain.repository.DownloadAuditLogRepository;
import com.capitec.statement.domain.repository.StatementRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {

    private final DownloadAuditLogRepository auditLogRepository;
    private final StatementRepository statementRepository;

    public AuditService(DownloadAuditLogRepository auditLogRepository, StatementRepository statementRepository) {
        this.auditLogRepository = auditLogRepository;
        this.statementRepository = statementRepository;
    }

    @Async
    public void logDownloadAttempt(UUID statementId, String customerId, String ipAddress, 
                                   String userAgent, AuditStatus status, String denialReason) {
        DownloadAuditLog auditLog = new DownloadAuditLog();
        auditLog.setStatement(statementRepository.getReferenceById(statementId));
        auditLog.setCustomerId(customerId);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setStatus(status);
        auditLog.setDenialReason(denialReason);
        
        auditLogRepository.save(auditLog);
    }
}
