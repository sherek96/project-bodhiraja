package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.modal.Role;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.modal.User;
import com.pirivena_project.pirivena.repository.UserRepository; // NEW: Adjust if your repository package path differs
import com.pirivena_project.pirivena.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // NEW: For capturing the active JWT identity context securely
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import com.pirivena_project.pirivena.dto.UserPageResponse;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // NEW: Added to allow direct self-identity lookups via username

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.username:admin}")
    private String bootstrapAdminUsername;

    @GetMapping
    public ResponseEntity<UserPageResponse> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String profileStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,asc") String sort,
            Principal principal) {
        String[] sortParts = sort.split(",", 2);
        boolean descending = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]);
        String currentUsername = principal == null ? null : principal.getName();
        var result = userService.searchUsers(search, active, role, accountType, profileStatus, currentUsername,
                page, size, sortParts[0], descending);
        Map<String, Long> summary = Map.of(
                "total", userService.countUsers(search, null, role, accountType, profileStatus, currentUsername),
                "active", userService.countUsers(search, true, role, accountType, profileStatus, currentUsername),
                "inactive", userService.countUsers(search, false, role, accountType, profileStatus, currentUsername),
                "employees", userService.countUsers(search, active, role, "EMPLOYEE", profileStatus, currentUsername),
                "students", userService.countUsers(search, active, role, "STUDENT", profileStatus, currentUsername),
                "unlinked", userService.countUsers(search, active, role, "UNLINKED", profileStatus, currentUsername));
        return ResponseEntity.ok(new UserPageResponse(result.getContent(), result.getTotalElements(), result.getTotalPages(),
                result.getNumber(), result.getSize(), summary));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("username", user.getUsername());
        profile.put("roles", user.getRoles());
        if (user.getEmployee() != null) {
            profile.put("displayName", user.getEmployee().getFullName());
            profile.put("profilePicture", user.getEmployee().getProfilePicture());
            profile.put("accountType", "EMPLOYEE");
        } else if (user.getStudent() != null) {
            profile.put("displayName", user.getStudent().getFullName());
            profile.put("profilePicture", user.getStudent().getProfilePicture());
            profile.put("accountType", "STUDENT");
        }
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRolesWithoutAdmin() {
        return new ResponseEntity<>(userService.getRolessWithoutAdmin(), HttpStatus.OK);
    }

    @GetMapping("/unlinked-employees")
    public ResponseEntity<List<Employee>> getUnlinkedEmployees() {
        return new ResponseEntity<>(userService.getUnlinkedEmployees(), HttpStatus.OK);
    }

    @GetMapping("/unlinked-students")
    public ResponseEntity<List<Student>> getUnlinkedStudents() {
        return new ResponseEntity<>(userService.getUnlinkedStudents(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error creating user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,@RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error updating user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reset-credentials")
    public ResponseEntity<?> resetCredentials(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.resetCredentials(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ==================== NEW: SELF-SERVICE PROFILE UPDATE GATEWAY ====================
    // Accessible via: PUT /api/users/profile/update
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateSelfProfile(@RequestBody User profileUpdateRequest, Principal principal) {
        try {
            // 1. Extract verified username from security signature
            String currentLoggedInUsername = principal.getName();

            // 2. Load the fully populated database row containing ID, Roles, and profile linkages
            User databaseSnapshot = userRepository.findByUsername(currentLoggedInUsername)
                    .orElseThrow(() -> new RuntimeException("Authenticated user session context could not be found"));

            boolean isBootstrapAdministrator = currentLoggedInUsername.equals(bootstrapAdminUsername)
                    && databaseSnapshot.getEmployee() == null
                    && databaseSnapshot.getStudent() == null
                    && databaseSnapshot.getRoles().stream()
                            .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));

            if (isBootstrapAdministrator) {
                if (profileUpdateRequest.getUsername() != null
                        && !bootstrapAdminUsername.equals(profileUpdateRequest.getUsername().trim())) {
                    throw new RuntimeException("The recovery administrator username cannot be changed");
                }
                if (profileUpdateRequest.getPassword() != null
                        && !profileUpdateRequest.getPassword().trim().isEmpty()) {
                    databaseSnapshot.setPassword(passwordEncoder.encode(profileUpdateRequest.getPassword()));
                    userRepository.save(databaseSnapshot);
                }
                return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
            }

            // 3. Bind username modifications from the payload directly to the snapshot
            databaseSnapshot.setUsername(profileUpdateRequest.getUsername());

            // 4. Feed forward the new plain text password string if the user filled it out
            if (profileUpdateRequest.getPassword() != null && !profileUpdateRequest.getPassword().trim().isEmpty()) {
                databaseSnapshot.setPassword(profileUpdateRequest.getPassword());
            }

            // 5. Hand the completely reconstructed object over to your existing validation method
            User updatedUser = userService.updateUser(databaseSnapshot);

            return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error updating profile credentials: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            User deactivatedUser = userService.deleteUser(id);
            return new ResponseEntity<>(deactivatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error deleting user: " + e.getMessage());
        }
    }
}
