package com.bodhiraja.bodhiraja.user.controller;

import com.bodhiraja.bodhiraja.user.User;
import com.bodhiraja.bodhiraja.user.dao.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserRepository userDao;

    // 1. Get all Users (for the User Management table)
    // URL: http://localhost:8080/user/all
    @GetMapping(value = "/all", produces = "application/json")
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @GetMapping(value = "/getByUsername/{username}", produces = "application/json")
    public User getUserByUsername(@PathVariable("username") String username) {
        return userDao.findByUsername(username).orElse(null);
    }
}