package com.mams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mams.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}