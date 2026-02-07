package com.bodhiraja.bodhiraja.privilege.controller;

import com.bodhiraja.bodhiraja.privilege.Privilege;
import com.bodhiraja.bodhiraja.privilege.Role;
// EXPLICITLY import your Module class to fix the conflict:
import com.bodhiraja.bodhiraja.privilege.Module;

import com.bodhiraja.bodhiraja.privilege.dao.ModuleRepository;
import com.bodhiraja.bodhiraja.privilege.dao.PrivilegeRepository;
import com.bodhiraja.bodhiraja.privilege.dao.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/privilege")
public class PrivilegeController {

    // --- 1. The Rules (Who can do what) ---
    @Autowired
    private PrivilegeRepository privilegeDao;

    @GetMapping(value = "/all", produces = "application/json")
    public List<Privilege> getAllPrivileges() {
        return privilegeDao.findAll();
    }

    // --- 2. Roles (Admin, Principal, Clerk) ---
    @Autowired
    private RoleRepository roleDao;

    @GetMapping(value = "/role/all", produces = "application/json")
    public List<Role> getAllRoles() {
        return roleDao.findAll();
    }

    // --- 3. System Modules (Student, Inventory, Finance) ---
    @Autowired
    private ModuleRepository moduleDao;

    @GetMapping(value = "/module/all", produces = "application/json")
    public List<Module> getAllModules() {
        return moduleDao.findAll();
    }
}