package com.bodhiraja.bodhiraja.attendance.dao;

import com.bodhiraja.bodhiraja.attendance.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Integer> {
}
