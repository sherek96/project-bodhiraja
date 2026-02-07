package com.bodhiraja.bodhiraja.academic.controller;

import com.bodhiraja.bodhiraja.academic.*;
import com.bodhiraja.bodhiraja.academic.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/academic")
public class AcademicController {

    // --- We need the Chefs (Repositories) ---
    @Autowired
    private AcademicYearRepository academicYearDao;

    @Autowired
    private AcademicYearStatusRepository academicYearStatusDao;

    @Autowired
    private ClassDetailsStatusRepository classDetailsStatusDao;

    @Autowired
    private GradeRepository gradeDao;

    @Autowired
    private ClassDetailsRepository classDetailsDao;


    // --- The Menu (URL Endpoints) ---

    // 1. URL: /academic/year/all
    @GetMapping(value = "/year/all", produces = "application/json")
    public List<AcademicYear> getAllYears() {
        return academicYearDao.findAll();
    }

    // 2. URL: /academic/grade/all
    @GetMapping(value = "/grade/all", produces = "application/json")
    public List<Grade> getAllGrades() {
        return gradeDao.findAll();
    }

    // 3. URL: /academic/class/all
    @GetMapping(value = "/class/all", produces = "application/json")
    public List<ClassDetails> getAllClasses() {
        return classDetailsDao.findAll();
    }

    @GetMapping(value = "/yearstatus/all", produces = "application/json")
    public List<AcademicYearStatus> getAllYearStatus() {
        return academicYearStatusDao.findAll();
    }

    @GetMapping(value = "/classstatus/all", produces = "application/json") // Added this
    public List<ClassDetailsStatus> getAllClassStatuses() {
        return classDetailsStatusDao.findAll();
    }
}