package com.pirivena_project.pirivena;

import com.pirivena_project.pirivena.modal.*;
import com.pirivena_project.pirivena.enums.*;
import com.pirivena_project.pirivena.repository.*;
import com.pirivena_project.pirivena.service.AcademicYearLifecycleGuard;
import com.pirivena_project.pirivena.service.impl.ClassroomServiceImpl;
import com.pirivena_project.pirivena.service.impl.ClassroomSubjectServiceImpl;
import com.pirivena_project.pirivena.service.impl.EnrollmentServiceImpl;
import com.pirivena_project.pirivena.service.impl.SubjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicSoftDeletionTests {

    @Mock private SubjectRepository subjectRepository;
    @Mock private ClassroomRepository classroomRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private ClassroomSubjectRepository classroomSubjectRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private AcademicYearLifecycleGuard lifecycleGuard;

    @InjectMocks private SubjectServiceImpl subjectService;
    @InjectMocks private ClassroomServiceImpl classroomService;
    @InjectMocks private ClassroomSubjectServiceImpl classroomSubjectService;
    @InjectMocks private EnrollmentServiceImpl enrollmentService;

    @Test
    void subjectDeletionArchivesWithoutDeleting() {
        Subject subject = new Subject();
        subject.setId(1);
        subject.setStatus(SubjectStatus.ACTIVE);
        when(subjectRepository.findById(1)).thenReturn(Optional.of(subject));

        subjectService.deleteSubject(1);

        assertEquals(SubjectStatus.ARCHIVED, subject.getStatus());
        verify(subjectRepository).save(subject);
        verify(subjectRepository, never()).deleteById(1);
    }

    @Test
    void classroomDeletionArchivesWithoutDeleting() {
        Classroom classroom = new Classroom();
        classroom.setId(2);
        classroom.setAcademicYear(year(AcademicYearStatus.PLANNED));
        classroom.setStatus(ClassroomStatus.PLANNED);
        when(classroomRepository.findById(2)).thenReturn(Optional.of(classroom));

        classroomService.deleteClassroom(2);

        assertEquals(ClassroomStatus.ARCHIVED, classroom.getStatus());
        verify(classroomRepository).save(classroom);
        verify(classroomRepository, never()).delete(classroom);
    }

    @Test
    void allocationRemovalDeactivatesWithoutDeleting() {
        Classroom classroom = new Classroom();
        classroom.setAcademicYear(year(AcademicYearStatus.PLANNED));
        ClassroomSubject allocation = new ClassroomSubject();
        allocation.setId(3);
        allocation.setClassroom(classroom);
        allocation.setIsActive(true);
        when(classroomSubjectRepository.findById(3)).thenReturn(Optional.of(allocation));

        classroomSubjectService.removeClassroomSubject(3);

        assertEquals(false, allocation.getIsActive());
        verify(classroomSubjectRepository).save(allocation);
        verify(classroomSubjectRepository, never()).delete(allocation);
    }

    @Test
    void enrollmentCancellationWithdrawsWithoutDeleting() {
        Classroom classroom = new Classroom();
        classroom.setAcademicYear(year(AcademicYearStatus.CURRENT));
        Enrollment enrollment = new Enrollment();
        enrollment.setId(4);
        enrollment.setClassroom(classroom);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setIsActive(true);
        when(enrollmentRepository.findById(4)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(4);

        assertEquals(EnrollmentStatus.WITHDRAWN, enrollment.getStatus());
        assertEquals(false, enrollment.getIsActive());
        verify(enrollmentRepository).save(enrollment);
        verify(enrollmentRepository, never()).delete(enrollment);
    }

    private AcademicYear year(AcademicYearStatus status) {
        AcademicYear year = new AcademicYear();
        year.setId(status.ordinal() + 1);
        year.setName(status.name());
        year.setStatus(status);
        year.setIsCurrent(status == AcademicYearStatus.CURRENT);
        return year;
    }
}
