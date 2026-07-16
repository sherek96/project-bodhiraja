package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.AcademicYear;
import java.util.List;

public interface AcademicYearService {
    AcademicYear saveAcademicYear(AcademicYear academicYear);
    List<AcademicYear> getAllAcademicYears();
    AcademicYear getActiveAcademicYear();
}