package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.dto.ReportCardResponseDTO;
import com.pirivena_project.pirivena.dto.SubjectMarkDTO;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.modal.ExamMark;
import com.pirivena_project.pirivena.enums.StudentType;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.ExamMarkRepository;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.service.ReportCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportCardServiceImpl implements ReportCardService {

    private final ExamMarkRepository examMarkRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassroomSubjectRepository classroomSubjectRepository;

    private static final BigDecimal PASS_THRESHOLD = new BigDecimal("40.00");

    @Override
    public ReportCardResponseDTO generateReportCard(Integer enrollmentId, Integer termNumber) {
        if (termNumber == null || termNumber < 1 || termNumber > 3) {
            throw new IllegalArgumentException("Term number must be 1, 2, or 3.");
        }
        // 1. Verify that the student enrollment record exists
        Enrollment targetEnrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Data Error: Enrollment record ID " + enrollmentId + " not found."));

        // 2. Fetch all marks earned by this student for the target term
        List<ExamMark> studentMarks = examMarkRepository.findByEnrollmentIdAndTermNumber(enrollmentId, termNumber);
        if (studentMarks.isEmpty()) {
            throw new RuntimeException("Operational Error: No examination marks found for this student in Term " + termNumber);
        }

        int requiredSubjectCount = classroomSubjectRepository.findByClassroomId(targetEnrollment.getClassroom().getId()).size();
        if (requiredSubjectCount == 0 || studentMarks.size() != requiredSubjectCount) {
            throw new IllegalStateException("A report card can only be generated after marks are recorded for every classroom subject.");
        }

        // 3. Compile the granular subject metrics breakdown list
        BigDecimal totalSum = BigDecimal.ZERO;
        List<SubjectMarkDTO> detailsList = new ArrayList<>();

        for (ExamMark mark : studentMarks) {
            totalSum = totalSum.add(mark.getMarksObtained());

            String status = mark.getMarksObtained().compareTo(PASS_THRESHOLD) >= 0 ? "Pass" : "Fail";
            detailsList.add(new SubjectMarkDTO(
                    mark.getSubject().getName(),
                    mark.getMarksObtained(),
                    status
            ));
        }

        // 4. Compute the mathematical average percentage
        BigDecimal subjectCount = new BigDecimal(studentMarks.size());
        BigDecimal average = totalSum.divide(subjectCount, 2, RoundingMode.HALF_UP);

        // 5. Execute the Dynamic Classroom Cohort Ranking Matrix Algorithm
        Integer classroomId = targetEnrollment.getClassroom().getId();
        List<ExamMark> cohortMarks = examMarkRepository.findByEnrollmentClassroomIdAndTermNumber(classroomId, termNumber);

        // Group all class scores by individual enrollment IDs and sum them up
        // Only students with a complete subject set participate in the class ranking.
        Map<Integer, List<ExamMark>> cohortMarksByEnrollment = cohortMarks.stream()
                .collect(Collectors.groupingBy(
                        mark -> mark.getEnrollment().getId()));
        Map<Integer, BigDecimal> cohortAveragesMap = cohortMarksByEnrollment.entrySet().stream()
                .filter(entry -> entry.getValue().size() == requiredSubjectCount)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(ExamMark::getMarksObtained)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(BigDecimal.valueOf(requiredSubjectCount), 2, RoundingMode.HALF_UP)
                ));

        List<BigDecimal> sortedAveragesList = cohortAveragesMap.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int calculatedRank = sortedAveragesList.indexOf(average) + 1;

        // 6. Map all calculated metrics into the final structural DTO payload
        ReportCardResponseDTO reportCard = new ReportCardResponseDTO();
        String studentName = targetEnrollment.getStudent().getStudentType() == StudentType.MONK
                ? targetEnrollment.getStudent().getOrdinationName()
                : targetEnrollment.getStudent().getFullName();
        reportCard.setStudentName(studentName);
        reportCard.setAdmissionNumber(targetEnrollment.getStudent().getAdmissionNo());
        reportCard.setClassroomName(targetEnrollment.getClassroom().getName());
        reportCard.setTermNumber(termNumber);
        reportCard.setSubjectDetails(detailsList);
        reportCard.setTotalMarks(totalSum);
        reportCard.setAveragePercentage(average);
        reportCard.setClassRank(calculatedRank);
        reportCard.setTotalStudentsInClass(cohortAveragesMap.size());

        return reportCard;
    }
}
