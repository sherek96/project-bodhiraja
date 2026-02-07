package com.bodhiraja.bodhiraja.employee.controller;

import com.bodhiraja.bodhiraja.employee.*; // Imports Employee, Designation, etc.
import com.bodhiraja.bodhiraja.employee.dao.*; // Imports all Repos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    // --- 1. Main Employee List ---
    @Autowired
    private EmployeeRepository employeeDao;

    @GetMapping(value = "/all", produces = "application/json")
    public List<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    // --- 2. Designation (Principal, Teacher, Clerk) ---
    @Autowired
    private DesignationRepository designationDao;

    @GetMapping(value = "/designation/all", produces = "application/json")
    public List<Designation> getAllDesignations() {
        return designationDao.findAll();
    }

    // --- 3. Employee Type (Academic vs Non-Academic) ---
    @Autowired
    private EmployeeTypeRepository employeeTypeDao;

    @GetMapping(value = "/type/all", produces = "application/json")
    public List<EmployeeType> getAllEmployeeTypes() {
        return employeeTypeDao.findAll();
    }

    // --- 4. Status (Active, Resigned, Suspended) ---
    @Autowired
    private StatusRepository statusDao;

    @GetMapping(value = "/status/all", produces = "application/json")
    public List<Status> getAllStatuses() {
        return statusDao.findAll();
    }
}