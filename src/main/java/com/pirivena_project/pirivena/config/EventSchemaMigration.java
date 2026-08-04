package com.pirivena_project.pirivena.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Migrates the legacy event-status column without deleting existing events.
 * The status feature has been removed, but older databases still have a
 * NOT NULL constraint on event_details.event_status_id.
 */
@Component
@RequiredArgsConstructor
public class EventSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<String> nullability = jdbcTemplate.queryForList(
                """
                SELECT IS_NULLABLE
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'event_details'
                  AND COLUMN_NAME = 'event_status_id'
                """,
                String.class);

        if (!nullability.isEmpty() && "NO".equalsIgnoreCase(nullability.get(0))) {
            jdbcTemplate.execute(
                    "ALTER TABLE event_details MODIFY COLUMN event_status_id INT NULL");
        }
    }
}
