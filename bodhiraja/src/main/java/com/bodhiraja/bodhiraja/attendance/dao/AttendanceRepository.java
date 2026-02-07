package com.bodhiraja.bodhiraja.attendance.dao;

import com.bodhiraja.bodhiraja.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByAttendanceDate(LocalDate date);
}
