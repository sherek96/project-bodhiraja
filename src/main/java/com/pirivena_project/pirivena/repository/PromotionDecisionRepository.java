package com.pirivena_project.pirivena.repository;

// Purpose: Reads and writes promotion decision records in the database.

import com.pirivena_project.pirivena.model.PromotionDecision;
import com.pirivena_project.pirivena.enums.PromotionOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionDecisionRepository extends JpaRepository<PromotionDecision, Integer> {
    boolean existsBySourceEnrollmentIdAndOutcome(Integer enrollmentId, PromotionOutcome outcome);
    boolean existsBySourceEnrollmentIdAndOutcomeNot(Integer enrollmentId, PromotionOutcome outcome);
    List<PromotionDecision> findByStudentIdOrderByDecisionDateDescIdDesc(Integer studentId);
    List<PromotionDecision> findBySourceClassroomIdOrderByDecisionDateDescIdDesc(Integer classroomId);
    long countByOutcome(PromotionOutcome outcome);
}
