package com.capitec.statement.domain.repository;

import com.capitec.statement.domain.entity.AuditStatus;
import com.capitec.statement.domain.entity.DownloadAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DownloadAuditLogRepository extends JpaRepository<DownloadAuditLog, UUID> {
    
    @Query("SELECT l FROM DownloadAuditLog l WHERE l.statement.statementId = :statementId ORDER BY l.accessedAt DESC")
    List<DownloadAuditLog> findByStatementIdOrderByAccessedAtDesc(@Param("statementId") UUID statementId);
    
    List<DownloadAuditLog> findByCustomerIdOrderByAccessedAtDesc(String customerId);
    
    @Query("SELECT COUNT(l) FROM DownloadAuditLog l WHERE l.statement.statementId = :statementId AND l.status = :status")
    long countByStatementIdAndStatus(@Param("statementId") UUID statementId, @Param("status") AuditStatus status);
}
