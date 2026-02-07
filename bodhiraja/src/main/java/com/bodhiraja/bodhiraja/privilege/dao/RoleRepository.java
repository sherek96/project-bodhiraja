package com.bodhiraja.bodhiraja.privilege.dao;

import com.bodhiraja.bodhiraja.privilege.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Find a role by name (Useful when creating a new user and assigning "Teacher" role)
    Role findByName(String name);
}