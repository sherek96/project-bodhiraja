package com.bodhiraja.bodhiraja.guardian.controller;

import com.bodhiraja.bodhiraja.guardian.*; // Imports Guardian, Type, Status
import com.bodhiraja.bodhiraja.guardian.dao.*; // Imports Repositories
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/guardian")
public class GuardianController {

    // --- 1. Guardian List (The Parents) ---
    @Autowired
    private GuardianRepository guardianDao;

    @GetMapping(value = "/all", produces = "application/json")
    public List<Guardian> getAllGuardians() {
        return guardianDao.findAll();
    }

    // --- 2. Relationship Types (Father, Mother, Aunt) ---
    @Autowired
    private GuardianTypeRepository guardianTypeDao;

    @GetMapping(value = "/type/all", produces = "application/json")
    public List<GuardianType> getAllTypes() {
        return guardianTypeDao.findAll();
    }

    // --- 3. Statuses (Alive, Deceased) ---
    @Autowired
    private GuardianStatusRepository guardianStatusDao;

    @GetMapping(value = "/status/all", produces = "application/json")
    public List<GuardianStatus> getAllStatuses() {
        return guardianStatusDao.findAll();
    }
}