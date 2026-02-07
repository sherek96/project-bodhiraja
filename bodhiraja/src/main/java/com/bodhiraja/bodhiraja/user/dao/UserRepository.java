package com.bodhiraja.bodhiraja.user.dao;

import com.bodhiraja.bodhiraja.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Critical for Login: Find user by their username
    Optional<User> findByUsername(String username);

    // Useful for "Forgot Password": Find user by email
    Optional<User> findByEmail(String email);

    // Useful: Check if a username exists before creating a new one (returns true/false)
    Boolean existsByUsername(String username);
}