ALTER TABLE subject
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE classroom
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PLANNED';

UPDATE classroom c
JOIN academic_year ay ON ay.id = c.academic_year_id
SET c.status = CASE ay.lifecycle_status
    WHEN 'CURRENT' THEN 'ACTIVE'
    WHEN 'COMPLETED' THEN 'COMPLETED'
    WHEN 'ARCHIVED' THEN 'ARCHIVED'
    ELSE 'PLANNED'
END;

ALTER TABLE enrollment
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

UPDATE enrollment
SET status = CASE WHEN is_active = TRUE THEN 'ACTIVE' ELSE 'PROMOTED' END;

ALTER TABLE classroom_subject
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'classroom'
       AND index_name = 'idx_classroom_academic_year') = 0,
    'CREATE INDEX idx_classroom_academic_year ON classroom (academic_year_id)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'classroom'
       AND index_name = 'idx_classroom_class_teacher') = 0,
    'CREATE INDEX idx_classroom_class_teacher ON classroom (class_teacher_id)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'classroom'
       AND index_name = 'uk_classroom_year_teacher') > 0,
    'ALTER TABLE classroom DROP INDEX uk_classroom_year_teacher',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;
