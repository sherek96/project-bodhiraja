package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r from Role r where r.name<>'ROLE_ADMIN'")
    List<Role> getRolesWithoutAdmin();
    Optional<Role> findByName(String name);
}
