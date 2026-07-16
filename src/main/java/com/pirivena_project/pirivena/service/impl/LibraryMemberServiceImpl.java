package com.pirivena_project.pirivena.service.impl;

import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.modal.LibraryMember;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.repository.EmployeeRepository;
import com.pirivena_project.pirivena.repository.LibraryMemberRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.service.LibraryMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryMemberServiceImpl implements LibraryMemberService {
    private static final String MEMBERSHIP_PREFIX = "LIB";

    private final LibraryMemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public LibraryMember save(LibraryMember member) {
        boolean hasStudent = member.getStudent() != null && member.getStudent().getId() != null;
        boolean hasEmployee = member.getEmployee() != null && member.getEmployee().getId() != null;
        if (hasStudent == hasEmployee) {
            throw new IllegalArgumentException("A library member must be linked to exactly one student or employee.");
        }

        Student student = hasStudent
                ? studentRepository.findById(member.getStudent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Student was not found."))
                : null;
        Employee employee = hasEmployee
                ? employeeRepository.findById(member.getEmployee().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee was not found."))
                : null;

        if (member.getId() == null) {
            if (student != null && memberRepository.existsByStudentId(student.getId())) {
                throw new IllegalArgumentException("This student already has a library membership.");
            }
            if (employee != null && memberRepository.existsByEmployeeId(employee.getId())) {
                throw new IllegalArgumentException("This employee already has a library membership.");
            }

            member.setMembershipNo(generateNextMembershipNo());
            member.setStatus("ACTIVE");
            member.setStudent(student);
            member.setEmployee(employee);
            return memberRepository.save(member);
        }

        LibraryMember existing = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Library member was not found."));
        if (existing.getStudent() != null && (student == null || !existing.getStudent().getId().equals(student.getId()))
                || existing.getEmployee() != null && (employee == null || !existing.getEmployee().getId().equals(employee.getId()))) {
            throw new IllegalArgumentException("A library membership cannot be transferred to another person.");
        }

        String status = member.getStatus() == null ? existing.getStatus() : member.getStatus();
        if (!List.of("ACTIVE", "SUSPENDED").contains(status)) {
            throw new IllegalArgumentException("Member status must be ACTIVE or SUSPENDED.");
        }
        existing.setStatus(status);
        return memberRepository.save(existing);
    }

    private String generateNextMembershipNo() {
        String maxMembershipNo = memberRepository.findMaxMembershipNo();
        if (maxMembershipNo != null && maxMembershipNo.startsWith(MEMBERSHIP_PREFIX)) {
            try {
                int number = Integer.parseInt(maxMembershipNo.substring(MEMBERSHIP_PREFIX.length()));
                return String.format("%s%04d", MEMBERSHIP_PREFIX, number + 1);
            } catch (NumberFormatException ignored) {
                // Fall back to the first valid number when legacy data has an unexpected format.
            }
        }
        return "LIB0001";
    }

    @Override
    public List<LibraryMember> getAll() {
        return memberRepository.findAll();
    }
}
