package com.pirivena_project.pirivena.service;

// Purpose: Stops operations that are not allowed for the academic year's current state.

import com.pirivena_project.pirivena.model.AcademicYear;
import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearLifecycleGuard {

    public void requireStructureEditable(AcademicYear year, String operation) {
        AcademicYearStatus status = statusOf(year);
        if (status != AcademicYearStatus.PLANNED) {
            throw new IllegalStateException(operation + " is only allowed while the academic year is PLANNED. "
                    + yearLabel(year) + " is " + status + ".");
        }
    }

    public void requireOperational(AcademicYear year, String operation) {
        AcademicYearStatus status = statusOf(year);
        if (status != AcademicYearStatus.CURRENT) {
            throw new IllegalStateException(operation + " is only allowed for the CURRENT academic year. "
                    + yearLabel(year) + " is " + status + ".");
        }
    }

    public void requirePromotion(AcademicYear source, AcademicYear destination) {
        requireOperational(source, "Student promotion");
        AcademicYearStatus destinationStatus = statusOf(destination);
        if (destinationStatus != AcademicYearStatus.PLANNED) {
            throw new IllegalStateException("Students can only be promoted into a PLANNED academic year. "
                    + yearLabel(destination) + " is " + destinationStatus + ".");
        }
    }

    private AcademicYearStatus statusOf(AcademicYear year) {
        if (year == null) {
            throw new IllegalArgumentException("An academic year is required for this operation.");
        }
        if (year.getStatus() == null) throw new IllegalStateException("Academic year status is required.");
        return year.getStatus();
    }

    private String yearLabel(AcademicYear year) {
        return "Academic year '" + (year.getName() == null ? year.getId() : year.getName()) + "'";
    }
}
