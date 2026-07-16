package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.ExamMark;
import com.pirivena_project.pirivena.repository.ExamMarkRepository;
import com.pirivena_project.pirivena.service.ExamMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamMarkServiceImpl implements ExamMarkService {

    private final ExamMarkRepository examMarkRepository;

    @Override
    @Transactional // Ensures all marks pass validation before any are committed to the database
    public List<ExamMark> saveExamMarkSheet(List<ExamMark> examMarkList) {
        List<ExamMark> savedMarks = new ArrayList<>();

        for (ExamMark mark : examMarkList) {
            // Rule 1: Validate Term Boundaries
            if (mark.getTermNumber() < 1 || mark.getTermNumber() > 3) {
                throw new RuntimeException("Validation Error: Term number must be 1, 2, or 3. Received: " + mark.getTermNumber());
            }

            // Rule 2: Validate Score Boundaries
            if (mark.getMarksObtained().compareTo(BigDecimal.ZERO) < 0 || mark.getMarksObtained().compareTo(new BigDecimal("100.00")) > 0) {
                throw new RuntimeException("Validation Error: Score must be between 0.00 and 100.00. Value rejected: " + mark.getMarksObtained());
            }

            // Rule 3: Enforce Composite Uniqueness Shield
            examMarkRepository.findByEnrollmentIdAndSubjectIdAndTermNumber(
                    mark.getEnrollment().getId(),
                    mark.getSubject().getId(),
                    mark.getTermNumber()
            ).ifPresent(existingMark -> {
                if (!existingMark.getId().equals(mark.getId())) {
                    throw new RuntimeException("Validation Error: A mark record already exists for Enrollment ID "
                            + mark.getEnrollment().getId() + " under Subject ID " + mark.getSubject().getId() + " for Term " + mark.getTermNumber());
                }
            });

            savedMarks.add(examMarkRepository.save(mark));
        }

        return savedMarks;
    }

    @Override
    public List<ExamMark> getGradingGrid(Integer classroomId, Integer subjectId, Integer termNumber) {
        return examMarkRepository.findGradingGrid(classroomId, subjectId, termNumber);
    }

    @Override
    public List<ExamMark> getStudentMarks(Integer studentId) {
        return examMarkRepository.findByEnrollmentStudentIdOrderByTermNumberDesc(studentId);
    }
}
