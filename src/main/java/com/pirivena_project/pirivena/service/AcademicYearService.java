package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.AcademicYear;
import java.util.List;
import com.pirivena_project.pirivena.dto.AcademicYearRolloverResult;
import com.pirivena_project.pirivena.dto.AcademicYearSummaryDTO;

public interface AcademicYearService {
    AcademicYear saveAcademicYear(AcademicYear academicYear);
    List<AcademicYear> getAllAcademicYears();
    AcademicYear getActiveAcademicYear();
    List<AcademicYearSummaryDTO> getAcademicYearSummaries();
    AcademicYearSummaryDTO getAcademicYearSummary(Integer id);
    AcademicYear activateAcademicYear(Integer id);
    AcademicYear closeAcademicYear(Integer id, boolean force);
    AcademicYear archiveAcademicYear(Integer id);
    AcademicYearRolloverResult copyStructure(Integer sourceId, Integer targetId);
}
