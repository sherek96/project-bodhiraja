ALTER TABLE classroom
    ADD CONSTRAINT uk_classroom_year_teacher
    UNIQUE (academic_year_id, class_teacher_id);
