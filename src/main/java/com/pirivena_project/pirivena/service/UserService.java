package com.pirivena_project.pirivena.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.modal.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pirivena_project.pirivena.modal.User;
import com.pirivena_project.pirivena.modal.Role;
import com.pirivena_project.pirivena.repository.RoleRepository;
import com.pirivena_project.pirivena.repository.UserRepository;
import com.pirivena_project.pirivena.repository.EmployeeRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.enums.EmployeeStatus;
import com.pirivena_project.pirivena.enums.StudentStatus;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private StudentRepository studentRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Role> getRolessWithoutAdmin() {
        return roleRepository.getRolesWithoutAdmin();
    }

    public List<Employee> getUnlinkedEmployees() {
        return userRepository.findEmployeesWithoutAccount().stream()
                .filter(employee -> employee.getStatus() == EmployeeStatus.ACTIVE).toList();
    }

    public List<Student> getUnlinkedStudents() {
        return userRepository.findStudentsWithoutAccount().stream()
                .filter(student -> student.getStatus() == StudentStatus.ACTIVE).toList();
    }

    public User createUser(User user) {
        // 1. UNIQUE USERNAME CHECK
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // 2. ONE-TO-ONE RELATIONSHIP SAFEGUARDS
        if (user.getEmployee() != null && user.getEmployee().getId() != null) {
            Employee employee = employeeRepository.findById(user.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee profile not found"));
            if (employee.getStatus() != EmployeeStatus.ACTIVE) {
                throw new RuntimeException("Only active employees can receive a user account");
            }
            user.setEmployee(employee);
            if (userRepository.existsByEmployeeId(user.getEmployee().getId())) {
                throw new RuntimeException("This Employee is already linked to another user account");
            }
        }
        if (user.getStudent() != null && user.getStudent().getId() != null) {
            Student student = studentRepository.findById(user.getStudent().getId())
                    .orElseThrow(() -> new RuntimeException("Student profile not found"));
            if (student.getStatus() != StudentStatus.ACTIVE) {
                throw new RuntimeException("Only active students can receive a user account");
            }
            user.setStudent(student);
            if (userRepository.existsByStudentId(user.getStudent().getId())) {
                throw new RuntimeException("This Student is already linked to another user account");
            }
        }

        // 3. RESOLVE AND MAP ROLES
        Set<Role> managedRoles = new HashSet<>();
        String assignedRoleName = "";

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                Role existingRole = roleRepository.findById(role.getId()).orElse(null);
                if (existingRole != null) {
                    managedRoles.add(existingRole);
                    assignedRoleName = existingRole.getName(); // Extracts text for validation
                }
            }
        }
        if (user.getEmployee() != null && user.getEmployee().getId() != null) {
            Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                    .orElseThrow(() -> new RuntimeException("Teacher role is not configured"));
            managedRoles.add(teacherRole);
        }
        user.setRoles(managedRoles);

        // 4. IDENTITY ALIGNMENT VALIDATION MATRIX
        if (hasRole(managedRoles, "ROLE_STUDENT")) {
            if (user.getStudent() == null || user.getStudent().getId() == null) {
                throw new RuntimeException("A student role must be uniquely linked to a Student profile");
            }
            if (user.getEmployee() != null && user.getEmployee().getId() != null) {
                throw new RuntimeException("A student user account cannot be linked to an Employee profile");
            }
        } else {
            // All other institutional roles (Admin, Principal, VP, Teacher, Librarian) are staff
            if (user.getEmployee() == null || user.getEmployee().getId() == null) {
                throw new RuntimeException("Academic and administrative roles must be linked to an Employee profile");
            }
            if (user.getStudent() != null && user.getStudent().getId() != null) {
                throw new RuntimeException("An academic staff account cannot be linked to a Student profile");
            }
        }

        // 5. SINGLE ACTIVE SEAT LOCKOUTS (Principal & Vice Principal)
        if (hasRole(managedRoles, "ROLE_PRINCIPAL")) {
            if (userRepository.existsByRolesNameAndIsActiveTrue("ROLE_PRINCIPAL")) {
                throw new RuntimeException("An active account with the Principal role already exists");
            }
        } else if (hasRole(managedRoles, "ROLE_VICEPRINCIPAL")) {
            if (userRepository.existsByRolesNameAndIsActiveTrue("ROLE_VICEPRINCIPAL")) {
                throw new RuntimeException("An active account with the Vice Principal role already exists");
            }
        }

        // 6. ASSIGN BASELINE SYSTEM DEFAULTS
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        // 1. FETCH PERSISTED DB SNAPSHOT
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (Boolean.TRUE.equals(user.getIsActive())) {
            ensureLinkedProfileActive(existingUser);
        }

        // 2. UNIQUE USERNAME CHECK (EXCLUDING SELF)
        if (userRepository.existsByUsernameAndIdNot(user.getUsername(), user.getId())) {
            throw new RuntimeException("Username already exists");
        }

        // 3. ONE-TO-ONE RELATIONSHIP SAFEGUARDS (EXCLUDING SELF)
        if (user.getEmployee() != null && user.getEmployee().getId() != null) {
            if (userRepository.existsByEmployeeIdAndIdNot(user.getEmployee().getId(), user.getId())) {
                throw new RuntimeException("This Employee is already linked to another user account");
            }
        }
        if (user.getStudent() != null && user.getStudent().getId() != null) {
            if (userRepository.existsByStudentIdAndIdNot(user.getStudent().getId(), user.getId())) {
                throw new RuntimeException("This Student is already linked to another user account");
            }
        }

        // 4. RESOLVE AND MAP ROLES
        Set<Role> managedRoles = new HashSet<>();
        String assignedRoleName = "";

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                Role existingRole = roleRepository.findById(role.getId()).orElse(null);
                if (existingRole != null) {
                    managedRoles.add(existingRole);
                    assignedRoleName = existingRole.getName();
                }
            }
        }
        if (user.getEmployee() != null && user.getEmployee().getId() != null) {
            Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                    .orElseThrow(() -> new RuntimeException("Teacher role is not configured"));
            managedRoles.add(teacherRole);
        }

        // 5. IDENTITY ALIGNMENT VALIDATION MATRIX FOR UPDATES
        if (hasRole(managedRoles, "ROLE_STUDENT")) {
            if (user.getStudent() == null || user.getStudent().getId() == null) {
                throw new RuntimeException("A student role must be uniquely linked to a Student profile");
            }
            if (user.getEmployee() != null && user.getEmployee().getId() != null) {
                throw new RuntimeException("A student user account cannot be linked to an Employee profile");
            }
        } else {
            if (user.getEmployee() == null || user.getEmployee().getId() == null) {
                throw new RuntimeException("Academic and administrative roles must be linked to an Employee profile");
            }
            if (user.getStudent() != null && user.getStudent().getId() != null) {
                throw new RuntimeException("An academic staff account cannot be linked to a Student profile");
            }
        }

        // 6. SINGLE ACTIVE SEAT LOCKOUTS (EXCLUDING SELF)
        if (Boolean.TRUE.equals(user.getIsActive())) {
            if (hasRole(managedRoles, "ROLE_PRINCIPAL")) {
                if (userRepository.existsByRolesNameAndIsActiveTrueAndIdNot("ROLE_PRINCIPAL", user.getId())) {
                    throw new RuntimeException("An active account with the Principal role already exists");
                }
            } else if (hasRole(managedRoles, "ROLE_VICEPRINCIPAL")) {
                if (userRepository.existsByRolesNameAndIsActiveTrueAndIdNot("ROLE_VICEPRINCIPAL", user.getId())) {
                    throw new RuntimeException("An active account with the Vice Principal role already exists");
                }
            }
        }

        // 7. MUTATE AND APPLY VALUES SAFELY (FIXED ENCRYPTION SHIELD)
        existingUser.setUsername(user.getUsername());
        existingUser.setIsActive(user.getIsActive());
        existingUser.setRoles(managedRoles);
        existingUser.setEmployee(user.getEmployee());
        existingUser.setStudent(user.getStudent());

        // Check if a password was provided in the update payload
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            // If the password string does NOT start with the BCrypt cryptographic prefix, it's raw text!
            if (!user.getPassword().startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        return userRepository.save(existingUser);
    }

    public User resetCredentials(Integer id) {
        User user = getUserById(id);
        String fullName = user.getEmployee() != null
                ? user.getEmployee().getFullName()
                : user.getStudent() != null ? user.getStudent().getFullName() : null;
        if (fullName == null || fullName.isBlank()) {
            throw new RuntimeException("The linked profile does not have a valid name for generating a username");
        }

        String baseUsername = fullName.trim().split("\\s+")[0]
                .toLowerCase()
                .replaceAll("[^\\p{L}\\p{N}]", "");
        if (baseUsername.isBlank()) {
            throw new RuntimeException("The linked profile first name cannot be converted into a valid username");
        }

        String availableUsername = baseUsername;
        int suffix = 2;
        while (userRepository.existsByUsernameAndIdNot(availableUsername, id)) {
            availableUsername = baseUsername + suffix++;
        }

        user.setUsername(availableUsername);
        user.setPassword(passwordEncoder.encode("1234"));
        return userRepository.save(user);
    }

    public User deleteUser(Integer id) {
        // 1. FETCH THE EXISTENT PROFILE SNAPSHOT
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. DYNAMIC IF-ELSE TOGGLE MATRIX
        if (Boolean.TRUE.equals(existingUser.getIsActive())) {
            // FORK A: Account is Active -> Deactivate it (Always Safe)
            existingUser.setIsActive(false);
        } else {
            // FORK B: Account is Inactive -> Attempting to Unlock (Requires Security Guard)
            ensureLinkedProfileActive(existingUser);

            // Extract the role name associated with this account container
            // ENFORCE SINGLE-ACTIVE-SEAT LOCKOUTS DURING RE-ACTIVATION (EXCLUDING SELF)
            if (hasRole(existingUser.getRoles(), "ROLE_PRINCIPAL")) {
                if (userRepository.existsByRolesNameAndIsActiveTrueAndIdNot("ROLE_PRINCIPAL", id)) {
                    throw new RuntimeException("Cannot unlock account: An active profile with the Principal role already exists.");
                }
            } else if (hasRole(existingUser.getRoles(), "ROLE_VICEPRINCIPAL")) {
                if (userRepository.existsByRolesNameAndIsActiveTrueAndIdNot("ROLE_VICEPRINCIPAL", id)) {
                    throw new RuntimeException("Cannot unlock account: An active profile with the Vice Principal role already exists.");
                }
            }

            // If the screening clears, it is safe to grant system access back
            existingUser.setIsActive(true);
        }

        // 3. PERSIST MUTATED STATE CONTEXT BACK TO DB
        return userRepository.save(existingUser);
    }

    private void ensureLinkedProfileActive(User user) {
        if (user.getEmployee() != null && user.getEmployee().getStatus() != EmployeeStatus.ACTIVE) {
            throw new RuntimeException("Cannot activate this account while the linked employee is not active");
        }
        if (user.getStudent() != null && user.getStudent().getStatus() != StudentStatus.ACTIVE) {
            throw new RuntimeException("Cannot activate this account while the linked student is not active");
        }
    }

    private boolean hasRole(Set<Role> roles, String roleName) {
        return roles != null && roles.stream().anyMatch(role -> roleName.equals(role.getName()));
    }


}
