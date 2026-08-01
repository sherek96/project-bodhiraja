package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // Looks up a single student's daily marker to prevent duplicate data entry rows
    Optional<Attendance> findByEnrollmentIdAndAttendanceDate(Integer enrollmentId, LocalDate attendanceDate);

    // Deep-fetch query to retrieve a complete daily attendance sheet for a specific classroom
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.classroom.id = :classroomId AND a.attendanceDate = :date")
    List<Attendance> findByClassroomAndDate(
            @Param("classroomId") Integer classroomId,
            @Param("date") LocalDate date
    );

    List<Attendance> findByEnrollmentStudentIdOrderByAttendanceDateDesc(Integer studentId);
    List<Attendance> findByEnrollmentId(Integer enrollmentId);
}
