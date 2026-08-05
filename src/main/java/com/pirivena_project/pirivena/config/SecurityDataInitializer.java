package com.pirivena_project.pirivena.config;

// Purpose: Creates the required roles and the initial administrator account when they are missing.

import com.pirivena_project.pirivena.model.Role;
import com.pirivena_project.pirivena.model.User;
import com.pirivena_project.pirivena.repository.RoleRepository;
import com.pirivena_project.pirivena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityDataInitializer.class);
    private static final List<String> STANDARD_ROLES = List.of(
            "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICEPRINCIPAL",
            "ROLE_TEACHER", "ROLE_LIBRARIAN", "ROLE_STUDENT"
    );

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${app.bootstrap-admin.username:admin}")
    private String bootstrapUsername;

    @Value("${app.bootstrap-admin.password:}")
    private String bootstrapPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        STANDARD_ROLES.forEach(this::ensureRoleExists);

        if (!bootstrapEnabled || userRepository.count() > 0) return;
        if (bootstrapUsername == null || bootstrapUsername.isBlank()
                || bootstrapPassword == null || bootstrapPassword.length() < 12) {
            LOGGER.error("No users exist. Set BOOTSTRAP_ADMIN_PASSWORD to at least 12 characters "
                    + "and restart to create the recovery administrator.");
            return;
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN could not be initialized."));
        User admin = new User();
        admin.setUsername(bootstrapUsername.trim());
        admin.setPassword(passwordEncoder.encode(bootstrapPassword));
        admin.setIsActive(true);
        admin.setRoles(new LinkedHashSet<>(List.of(adminRole)));
        userRepository.save(admin);
        LOGGER.warn("Created bootstrap administrator '{}' because no user accounts existed.", admin.getUsername());
    }

    private void ensureRoleExists(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
