// Tests library membership rules directly, without using the frontend.
package com.pirivena_project.pirivena;

// Purpose: Tests the library member service behavior without using the frontend.

import com.pirivena_project.pirivena.model.LibraryMember;
import com.pirivena_project.pirivena.model.Student;
import com.pirivena_project.pirivena.repository.EmployeeRepository;
import com.pirivena_project.pirivena.repository.LibraryMemberRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import com.pirivena_project.pirivena.service.LibraryMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryMemberServiceTests {
    @Mock private LibraryMemberRepository memberRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private EmployeeRepository employeeRepository;
    @InjectMocks private LibraryMemberService service;

    @Test
    void newMembershipReceivesNextGeneratedLibNumber() {
        Student student = new Student();
        student.setId(4);
        LibraryMember request = new LibraryMember();
        request.setStudent(student);

        when(studentRepository.findById(4)).thenReturn(Optional.of(student));
        when(memberRepository.findMaxMembershipNo()).thenReturn("LIB0041");
        when(memberRepository.save(request)).thenReturn(request);

        LibraryMember saved = service.save(request);

        assertEquals("LIB0042", saved.getMembershipNo());
        assertEquals("ACTIVE", saved.getStatus());
    }

    @Test
    void updatePreservesGeneratedMembershipNumber() {
        Student student = new Student();
        student.setId(4);
        LibraryMember existing = new LibraryMember();
        existing.setId(9);
        existing.setMembershipNo("LIB0012");
        existing.setStatus("ACTIVE");
        existing.setStudent(student);

        LibraryMember request = new LibraryMember();
        request.setId(9);
        request.setMembershipNo("MANUAL-NUMBER");
        request.setStatus("SUSPENDED");
        request.setStudent(student);

        when(studentRepository.findById(4)).thenReturn(Optional.of(student));
        when(memberRepository.findById(9)).thenReturn(Optional.of(existing));
        when(memberRepository.save(existing)).thenReturn(existing);

        LibraryMember saved = service.save(request);

        assertEquals("LIB0012", saved.getMembershipNo());
        assertEquals("SUSPENDED", saved.getStatus());
    }
}
