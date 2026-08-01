package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.modal.AcademicYearStatus;
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
        if (year.getStatus() != null) {
            return year.getStatus();
        }
        return Boolean.TRUE.equals(year.getIsCurrent())
                ? AcademicYearStatus.CURRENT
                : AcademicYearStatus.COMPLETED;
    }

    private String yearLabel(AcademicYear year) {
        return "Academic year '" + (year.getName() == null ? year.getId() : year.getName()) + "'";
    }
}
