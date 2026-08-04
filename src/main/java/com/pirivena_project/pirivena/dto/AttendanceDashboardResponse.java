package com.pirivena_project.pirivena.dto;

// Purpose: Carries attendance dashboard results from the backend to the frontend.

import java.time.LocalDate;
import java.util.List;

public record AttendanceDashboardResponse(
        List<DailySummary> days,
        long weeklyPresent,
        long weeklyAbsent,
        int weeklyPercentage,
        long todayPresent,
        long todayAbsent,
        int todayPercentage,
        int expectedClassrooms,
        int submittedClassrooms,
        List<String> missingClassrooms) {

    public record DailySummary(
            LocalDate date,
            long present,
            long absent,
            int percentage,
            boolean recorded) {
    }
}
