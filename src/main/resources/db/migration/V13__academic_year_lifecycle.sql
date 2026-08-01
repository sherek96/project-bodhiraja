ALTER TABLE academic_year
    ADD COLUMN IF NOT EXISTS lifecycle_status VARCHAR(20) NOT NULL DEFAULT 'PLANNED';

UPDATE academic_year
SET lifecycle_status = CASE WHEN is_current = TRUE THEN 'CURRENT' ELSE 'COMPLETED' END;
