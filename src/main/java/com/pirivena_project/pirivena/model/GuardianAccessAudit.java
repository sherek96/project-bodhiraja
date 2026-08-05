package com.pirivena_project.pirivena.model;

// Purpose: Represents guardian access audit data stored in the database.

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "guardian_access_audit")
public class GuardianAccessAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(name = "guardian_id", nullable = false) private Integer guardianId;
    @Column(name = "accessed_by", nullable = false) private String accessedBy;
    @Column(nullable = false, length = 30) private String action;
    @Column(name = "accessed_at", nullable = false) private LocalDateTime accessedAt;
}
