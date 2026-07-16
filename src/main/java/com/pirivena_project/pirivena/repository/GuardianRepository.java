package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, Integer> {
    boolean existsByNic (String nic);
    boolean existsByPhonePrimary (String phonePrimary);
    boolean existsByNicAndIdNot(String nic, Integer id);
    boolean existsByPhonePrimaryAndIdNot(String phonePrimary, Integer id);
}
