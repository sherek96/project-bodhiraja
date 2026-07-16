ALTER TABLE student
    ADD COLUMN guardian_relationship VARCHAR(30) NOT NULL DEFAULT 'OTHER',
    ADD COLUMN guardian_primary_contact BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN guardian_emergency_contact BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN guardian_authorized_for_pickup BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN guardian_receives_notifications BOOLEAN NOT NULL DEFAULT TRUE;
