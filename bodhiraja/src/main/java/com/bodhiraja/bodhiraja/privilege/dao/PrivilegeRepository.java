package com.bodhiraja.bodhiraja.privilege.dao;

import com.bodhiraja.bodhiraja.privilege.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {

    // Get all permissions for a specific Role
    List<Privilege> findByRole_Id(Integer roleId);

    // Check if a specific Role has permission for a specific Module
    // (e.g., Does "Teacher" have permission for "Student" module?)
    Privilege findByRole_IdAndModule_Id(Integer roleId, Integer moduleId);
}