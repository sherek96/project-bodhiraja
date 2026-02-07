package com.bodhiraja.bodhiraja.privilege.dao;

import com.bodhiraja.bodhiraja.privilege.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
}