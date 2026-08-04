package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes guardian access audit records in the database.

import com.pirivena_project.pirivena.model.GuardianAccessAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianAccessAuditRepository extends JpaRepository<GuardianAccessAudit, Integer> {}
