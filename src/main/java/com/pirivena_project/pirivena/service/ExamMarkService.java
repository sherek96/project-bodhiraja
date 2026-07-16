package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.modal.ExamMark;
import java.util.List;

public interface ExamMarkService {
    List<ExamMark> saveExamMarkSheet(List<ExamMark> examMarkList);
    List<ExamMark> getGradingGrid(Integer classroomId, Integer subjectId, Integer termNumber);
    List<ExamMark> getStudentMarks(Integer studentId);
}
