package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.FundingPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface FundingPoolRepository extends JpaRepository<FundingPool, Integer> {
    // Used to look up a pool by name if needed during validation
    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FundingPool f WHERE f.id = :id")
    Optional<FundingPool> findByIdForUpdate(@Param("id") Integer id);
}
