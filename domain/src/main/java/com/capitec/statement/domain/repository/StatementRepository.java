package com.capitec.statement.domain.repository;

import com.capitec.statement.domain.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
    List<Statement> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    Optional<Statement> findByStatementIdAndCustomerId(UUID statementId, String customerId);
    boolean existsByCustomerIdAndAccountNumberAndStatementPeriod(String customerId, String accountNumber, String statementPeriod);
}
