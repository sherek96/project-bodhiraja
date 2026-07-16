ALTER TABLE guardian
    ADD COLUMN whatsapp_number VARCHAR(10) NULL,
    ADD COLUMN email VARCHAR(150) NULL,
    ADD COLUMN preferred_contact_method VARCHAR(30) NOT NULL DEFAULT 'PRIMARY_MOBILE',
    ADD COLUMN preferred_language VARCHAR(20) NOT NULL DEFAULT 'SINHALA',
    ADD COLUMN emergency_contact_priority INT NOT NULL DEFAULT 1,
    MODIFY nic VARCHAR(12) NOT NULL,
    MODIFY dob DATE NOT NULL,
    MODIFY gender VARCHAR(20) NOT NULL;

CREATE TABLE guardian_access_audit (
    id INT NOT NULL AUTO_INCREMENT,
    guardian_id INT NOT NULL,
    accessed_by VARCHAR(255) NOT NULL,
    action VARCHAR(30) NOT NULL,
    accessed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_guardian_access_audit_guardian (guardian_id),
    CONSTRAINT fk_guardian_access_audit_guardian FOREIGN KEY (guardian_id) REFERENCES guardian(id)
);
