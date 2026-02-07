package com.bodhiraja.bodhiraja.registration.controller;

import com.bodhiraja.bodhiraja.registration.StudentRegistration;
import com.bodhiraja.bodhiraja.registration.StudentRegistrationStatus;
import com.bodhiraja.bodhiraja.registration.dao.StudentRegistrationRepository;
import com.bodhiraja.bodhiraja.registration.dao.StudentRegistrationStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/registration")
public class RegistrationController {

    // --- 1. Class Registers (Who is in what class) ---
    @Autowired
    private StudentRegistrationRepository registrationDao;

    @GetMapping(value = "/all", produces = "application/json")
    public List<StudentRegistration> getAllRegistrations() {
        return registrationDao.findAll();
    }

    // --- 2. Registration Status (Active, Left, etc.) ---
    @Autowired
    private StudentRegistrationStatusRepository statusDao;

    @GetMapping(value = "/status/all", produces = "application/json")
    public List<StudentRegistrationStatus> getAllStatuses() {
        return statusDao.findAll();
    }
}