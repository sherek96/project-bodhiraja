package com.pirivena_project.pirivena;

import com.pirivena_project.pirivena.modal.AcademicYear;
import com.pirivena_project.pirivena.enums.AcademicYearStatus;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcademicYearLifecycleGuardTests {

    private final AcademicYearLifecycleGuard guard = new AcademicYearLifecycleGuard();

    @Test
    void onlyPlannedYearsAllowStructureChanges() {
        assertDoesNotThrow(() -> guard.requireStructureEditable(year(AcademicYearStatus.PLANNED), "Classroom setup"));
        assertThrows(IllegalStateException.class,
                () -> guard.requireStructureEditable(year(AcademicYearStatus.CURRENT), "Classroom setup"));
        assertThrows(IllegalStateException.class,
                () -> guard.requireStructureEditable(year(AcademicYearStatus.COMPLETED), "Classroom setup"));
        assertThrows(IllegalStateException.class,
                () -> guard.requireStructureEditable(year(AcademicYearStatus.ARCHIVED), "Classroom setup"));
    }

    @Test
    void onlyCurrentYearsAllowOperationalChanges() {
        assertDoesNotThrow(() -> guard.requireOperational(year(AcademicYearStatus.CURRENT), "Attendance"));
        assertThrows(IllegalStateException.class,
                () -> guard.requireOperational(year(AcademicYearStatus.PLANNED), "Attendance"));
        assertThrows(IllegalStateException.class,
                () -> guard.requireOperational(year(AcademicYearStatus.COMPLETED), "Attendance"));
        assertThrows(IllegalStateException.class,
                () -> guard.requireOperational(year(AcademicYearStatus.ARCHIVED), "Attendance"));
    }

    @Test
    void promotionRequiresCurrentSourceAndPlannedDestination() {
        assertDoesNotThrow(() -> guard.requirePromotion(
                year(AcademicYearStatus.CURRENT), year(AcademicYearStatus.PLANNED)));
        assertThrows(IllegalStateException.class, () -> guard.requirePromotion(
                year(AcademicYearStatus.COMPLETED), year(AcademicYearStatus.PLANNED)));
        assertThrows(IllegalStateException.class, () -> guard.requirePromotion(
                year(AcademicYearStatus.CURRENT), year(AcademicYearStatus.ARCHIVED)));
    }

    private AcademicYear year(AcademicYearStatus status) {
        AcademicYear year = new AcademicYear();
        year.setId(status.ordinal() + 1);
        year.setName(status.name());
        year.setStatus(status);
        year.setIsCurrent(status == AcademicYearStatus.CURRENT);
        return year;
    }
}
