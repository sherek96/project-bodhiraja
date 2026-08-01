package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.PromotionDecision;
import com.pirivena_project.pirivena.modal.PromotionOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionDecisionRepository extends JpaRepository<PromotionDecision, Integer> {
    boolean existsBySourceEnrollmentIdAndOutcome(Integer enrollmentId, PromotionOutcome outcome);
    boolean existsBySourceEnrollmentIdAndOutcomeNot(Integer enrollmentId, PromotionOutcome outcome);
    List<PromotionDecision> findByStudentIdOrderByDecisionDateDescIdDesc(Integer studentId);
    List<PromotionDecision> findBySourceClassroomIdOrderByDecisionDateDescIdDesc(Integer classroomId);
}
