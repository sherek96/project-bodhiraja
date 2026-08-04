package com.pirivena_project.pirivena.service;

import com.pirivena_project.pirivena.enums.EmployeeStatus;
import com.pirivena_project.pirivena.modal.Designation;
import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.repository.DesignationRepository;
import com.pirivena_project.pirivena.repository.EmployeeRepository;
import com.pirivena_project.pirivena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeService {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${app.upload.employee-dir:uploads/employees}")
    private String employeeUploadDirectory;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesignationRepository designationRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Integer id) {
        // Find the employee by ID, or throw an explicit error message if they don't exist
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }

    public Employee createEmployee(Employee employee) {
        validateIdentityDetails(employee);
        // 1. Validation Rules (Unique checks)
        if (employeeRepository.existsByNic(employee.getNic())) {
            throw new RuntimeException("Duplicate NIC");
        }
        if (employeeRepository.existsByPhonePrimary(employee.getPhonePrimary())) {
            throw new RuntimeException("Duplicate Phone Number");
        }
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Duplicate Email");
        }

        // 2. Business Rule: Strict Designation Restrictions
        Designation dbDesignation = designationRepository.findById(employee.getDesignation().getId())
                .orElseThrow(() -> new RuntimeException("Designation Not Found"));

        String code = dbDesignation.getCode();
        if ("PRIN".equals(code) || "VPRIN".equals(code)) {
            if (employeeRepository.existsByDesignation_CodeAndStatus(code, EmployeeStatus.ACTIVE)) {
                throw new RuntimeException("Error: An active " + dbDesignation.getName() + " already exists.");
            }
        }

        // 3. Logic: Generate Next Employee Number
        String maxEmpNo = employeeRepository.findMaxEmpNo();
        String nextEmpNo = "EMP0001";

        if (maxEmpNo != null && maxEmpNo.startsWith("EMP")) {
            try {
                int numberPart = Integer.parseInt(maxEmpNo.substring(3));
                nextEmpNo = String.format("EMP%04d", numberPart + 1);
            } catch (NumberFormatException e) {
                nextEmpNo = "EMP0001"; // Fallback if data formatting is corrupted
            }
        }

        // 4. Set Defaults
        employee.setEmpNo(nextEmpNo);
        employee.setStatus(EmployeeStatus.ACTIVE);

        // 5. Save and Return
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        validateIdentityDetails(employee);
        // 1. Fetch existing record (The Snapshot)
        Employee existingEmployee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        // 2. Validate Duplicates (Excluding the current record's ID)
        if (employeeRepository.existsByNicAndIdNot(employee.getNic(), employee.getId())) {
            throw new RuntimeException("Duplicate NIC");
        }
        if (employeeRepository.existsByPhonePrimaryAndIdNot(employee.getPhonePrimary(), employee.getId())) {
            throw new RuntimeException("Duplicate Phone Number");
        }
        if (employeeRepository.existsByEmailAndIdNot(employee.getEmail(), employee.getId())) {
            throw new RuntimeException("Duplicate Email");
        }

        // 3. Unique Role Check (Excluding the current record's ID)
        Designation dbDesignation = designationRepository.findById(employee.getDesignation().getId())
                .orElseThrow(() -> new RuntimeException("Designation Not Found"));

        String code = dbDesignation.getCode();
        if ("PRIN".equals(code) || "VPRIN".equals(code)) {
            if (employeeRepository.existsByDesignation_CodeAndStatusAndIdNot(code, EmployeeStatus.ACTIVE, employee.getId())) {
                throw new RuntimeException("Error: An active " + dbDesignation.getName() + " already exists.");
            }
        }

        // 4. Update the fields on our managed instance
        existingEmployee.setFullName(employee.getFullName());
        existingEmployee.setNic(employee.getNic());
        existingEmployee.setPhonePrimary(employee.getPhonePrimary());
        existingEmployee.setPhoneSecondary(employee.getPhoneSecondary());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setDesignation(dbDesignation);
        existingEmployee.setTitle(employee.getTitle());
        existingEmployee.setAddress(employee.getAddress());
        existingEmployee.setTeacherGrade(employee.getTeacherGrade());
        existingEmployee.setStatus(employee.getStatus());
        existingEmployee.setJoinDate(employee.getJoinDate());
        existingEmployee.setDob(employee.getDob());

        // 5. Save the updated instance back to the DB
        Employee savedEmployee = employeeRepository.save(existingEmployee);
        synchronizeLinkedUserStatus(savedEmployee);
        return savedEmployee;
    }

    public Employee deleteEmployee(Integer id) {
        // 1. Fetch the employee record or throw an error if it doesn't exist
        Employee employeeToDelete = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        // 2. Perform the Soft Delete by modifying the status
        employeeToDelete.setStatus(EmployeeStatus.TERMINATED);

        // 3. Save and return the updated entity
        Employee savedEmployee = employeeRepository.save(employeeToDelete);
        synchronizeLinkedUserStatus(savedEmployee);
        return savedEmployee;
    }

    private void synchronizeLinkedUserStatus(Employee employee) {
        boolean accountShouldBeActive = employee.getStatus() == EmployeeStatus.ACTIVE
                || employee.getStatus() == EmployeeStatus.ON_LEAVE;
        userRepository.findByEmployeeId(employee.getId()).ifPresent(user -> {
            user.setIsActive(accountShouldBeActive);
            userRepository.save(user);
        });
    }

    public Employee uploadProfilePicture(Integer id, MultipartFile file) {
        Employee employee = getEmployeeById(id);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Select a profile picture to upload");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Profile picture must not exceed 5 MB");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Only JPG, PNG and WebP profile pictures are allowed");
        }

        String extension = extensionForContentType(file.getContentType());
        String uniqueFilename = UUID.randomUUID() + extension;

        try {
            Path uploadDirectory = Paths.get(employeeUploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(uploadDirectory);
            Files.copy(file.getInputStream(), uploadDirectory.resolve(uniqueFilename), StandardCopyOption.REPLACE_EXISTING);

            String previousPicture = employee.getProfilePicture();
            employee.setProfilePicture("/uploads/employees/" + uniqueFilename);
            Employee savedEmployee = employeeRepository.save(employee);
            deletePreviousPicture(previousPicture, uploadDirectory);
            return savedEmployee;
        } catch (IOException e) {
            throw new RuntimeException("Could not save profile picture", e);
        }
    }

    private String extensionForContentType(String contentType) {
        return "image/png".equals(contentType) ? ".png" : "image/webp".equals(contentType) ? ".webp" : ".jpg";
    }

    private void deletePreviousPicture(String previousPicture, Path uploadDirectory) {
        if (previousPicture == null || previousPicture.isBlank()) return;
        try {
            String previousFilename = Paths.get(previousPicture).getFileName().toString();
            Files.deleteIfExists(uploadDirectory.resolve(previousFilename));
        } catch (IOException ignored) {
            // A stale old image must not make the new upload fail.
        }
    }

    private void validateIdentityDetails(Employee employee) {
        if (employee.getDob() == null) {
            throw new RuntimeException("Date of birth is required");
        }

        String nic = employee.getNic() == null ? "" : employee.getNic().trim();
        boolean oldFormat = nic.matches("\\d{9}[vVxX]");
        boolean newFormat = nic.matches("\\d{12}");
        if (!oldFormat && !newFormat) {
            throw new RuntimeException("Invalid NIC format");
        }

        int year = oldFormat
                ? 1900 + Integer.parseInt(nic.substring(0, 2))
                : Integer.parseInt(nic.substring(0, 4));
        int encodedDay = oldFormat
                ? Integer.parseInt(nic.substring(2, 5))
                : Integer.parseInt(nic.substring(4, 7));

        if (encodedDay > 500) {
            throw new RuntimeException("This ID belongs to a female. Check the ID again.");
        }

        try {
            LocalDate.ofYearDay(year, encodedDay);
        } catch (DateTimeException e) {
            throw new RuntimeException("Invalid NIC birth date");
        }

        int employeeAge = Period.between(employee.getDob(), LocalDate.now()).getYears();
        if (employeeAge < 18 || employeeAge > 60) {
            throw new RuntimeException("Employee must be between 18 and 60 years old");
        }

        if (employee.getJoinDate() == null) {
            throw new RuntimeException("Appointment date is required");
        }
        if (employee.getJoinDate().isBefore(employee.getDob().plusYears(18))) {
            throw new RuntimeException("Appointment date cannot be before the employee turns 18");
        }
        if (employee.getJoinDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Appointment date cannot be in the future");
        }
    }
}
