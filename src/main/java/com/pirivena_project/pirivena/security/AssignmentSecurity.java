package com.pirivena_project.pirivena.security;

import com.pirivena_project.pirivena.modal.Attendance;
import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.modal.ExamMark;
import com.pirivena_project.pirivena.modal.User;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.modal.Guardian;
import com.pirivena_project.pirivena.modal.Enrollment;
import com.pirivena_project.pirivena.modal.Subject;
import com.pirivena_project.pirivena.modal.LibraryMember;
import com.pirivena_project.pirivena.modal.BookLending;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import com.pirivena_project.pirivena.repository.ClassroomSubjectRepository;
import com.pirivena_project.pirivena.repository.EnrollmentRepository;
import com.pirivena_project.pirivena.repository.UserRepository;
import com.pirivena_project.pirivena.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("assignmentSecurity")
@RequiredArgsConstructor
public class AssignmentSecurity {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final ClassroomSubjectRepository classroomSubjectRepository;
    private final EnrollmentRepository enrollmentRepository;

    private boolean privileged(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority ->
                authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_PRINCIPAL")
                        || authority.getAuthority().equals("ROLE_VICEPRINCIPAL"));
    }

    public boolean canViewSensitiveGuardianData(Authentication authentication) {
        return privileged(authentication);
    }

    private Integer employeeId(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .map(User::getEmployee)
                .map(employee -> employee.getId())
                .orElse(null);
    }

    private Integer studentId(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName()).map(User::getStudent)
                .map(Student::getId).orElse(null);
    }

    public boolean classTeacher(Integer classroomId, Authentication authentication) {
        if (privileged(authentication)) return true;
        Integer employeeId = employeeId(authentication);
        return employeeId != null && classroomRepository.findById(classroomId)
                .map(Classroom::getClassTeacher)
                .map(teacher -> teacher.getId().equals(employeeId))
                .orElse(false);
    }

    public boolean classroomParticipant(Integer classroomId, Authentication authentication) {
        if (classTeacher(classroomId, authentication)) return true;
        Integer studentId = studentId(authentication);
        return studentId != null && enrollmentRepository.existsByStudentIdAndClassroomIdAndIsActiveTrue(studentId, classroomId);
    }

    public boolean subjectTeacher(Integer classroomId, Integer subjectId, Authentication authentication) {
        if (privileged(authentication)) return true;
        Integer employeeId = employeeId(authentication);
        return employeeId != null && classroomSubjectRepository.findByClassroomIdAndSubjectId(classroomId, subjectId)
                .map(allocation -> allocation.getTeacher() != null && allocation.getTeacher().getId().equals(employeeId))
                .orElse(false);
    }

    public boolean subjectParticipant(Integer classroomId, Integer subjectId, Authentication authentication) {
        if (subjectTeacher(classroomId, subjectId, authentication)) return true;
        Integer studentId = studentId(authentication);
        return studentId != null && enrollmentRepository.existsByStudentIdAndClassroomIdAndIsActiveTrue(studentId, classroomId)
                && classroomSubjectRepository.findByClassroomIdAndSubjectId(classroomId, subjectId).isPresent();
    }

    public boolean attendanceSheet(List<Attendance> records, Authentication authentication) {
        if (privileged(authentication)) return true;
        if (records == null || records.isEmpty()) return false;
        return records.stream().allMatch(record -> record.getEnrollment() != null
                && record.getEnrollment().getId() != null
                && enrollmentRepository.findById(record.getEnrollment().getId())
                .map(enrollment -> classTeacher(enrollment.getClassroom().getId(), authentication)).orElse(false));
    }

    public boolean markSheet(List<ExamMark> records, Authentication authentication) {
        if (privileged(authentication)) return true;
        if (records == null || records.isEmpty()) return false;
        return records.stream().allMatch(record -> record.getEnrollment() != null && record.getEnrollment().getId() != null
                && record.getSubject() != null && record.getSubject().getId() != null
                && enrollmentRepository.findById(record.getEnrollment().getId())
                .map(enrollment -> subjectTeacher(enrollment.getClassroom().getId(), record.getSubject().getId(), authentication))
                .orElse(false));
    }

    public boolean reportCard(Integer enrollmentId, Authentication authentication) {
        if (privileged(authentication)) return true;
        return enrollmentRepository.findById(enrollmentId)
                .map(enrollment -> classTeacher(enrollment.getClassroom().getId(), authentication)
                        || enrollment.getStudent().getId().equals(studentId(authentication))).orElse(false);
    }

    public List<Classroom> visibleClassrooms(List<Classroom> classrooms, Authentication authentication) {
        if (privileged(authentication)) return classrooms;
        Integer employeeId = employeeId(authentication);
        if (employeeId != null) return classrooms.stream().filter(classroom -> classroom.getClassTeacher() != null
                && classroom.getClassTeacher().getId().equals(employeeId)).toList();
        Integer studentId = studentId(authentication);
        if (studentId == null) return List.of();
        var classroomIds = enrollmentRepository.findByStudentId(studentId).stream()
                .filter(enrollment -> Boolean.TRUE.equals(enrollment.getIsActive()))
                .map(enrollment -> enrollment.getClassroom().getId()).collect(java.util.stream.Collectors.toSet());
        return classrooms.stream().filter(classroom -> classroomIds.contains(classroom.getId())).toList();
    }

    public List<Student> visibleStudents(List<Student> students, Authentication authentication) {
        if (privileged(authentication) || authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"))) return students;
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId != null) return students.stream().filter(student -> student.getId().equals(ownStudentId)).toList();
        Integer employeeId = employeeId(authentication);
        if (employeeId == null) return List.of();
        return students.stream().filter(student -> enrollmentRepository.findByStudentId(student.getId()).stream()
                .anyMatch(enrollment -> enrollment.getClassroom().getClassTeacher() != null
                        && enrollment.getClassroom().getClassTeacher().getId().equals(employeeId))).toList();
    }

    public List<Guardian> visibleGuardians(List<Guardian> guardians, Authentication authentication) {
        if (privileged(authentication)) return guardians;
        var guardianIds = visibleStudents(studentRepository.findAll(), authentication).stream()
                .filter(student -> student.getGuardian() != null)
                .map(student -> student.getGuardian().getId()).collect(java.util.stream.Collectors.toSet());
        return guardians.stream().filter(guardian -> guardianIds.contains(guardian.getId())).toList();
    }

    public List<Enrollment> visibleEnrollments(List<Enrollment> enrollments, Authentication authentication) {
        if (privileged(authentication)) return enrollments;
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId != null) return enrollments.stream().filter(e -> e.getStudent().getId().equals(ownStudentId)).toList();
        Integer employeeId = employeeId(authentication);
        return employeeId == null ? List.of() : enrollments.stream().filter(e -> e.getClassroom().getClassTeacher() != null
                && e.getClassroom().getClassTeacher().getId().equals(employeeId)).toList();
    }

    public List<Subject> visibleSubjects(List<Subject> subjects, Authentication authentication) {
        if (studentId(authentication) == null) return subjects;
        var subjectIds = enrollmentRepository.findByStudentId(studentId(authentication)).stream()
                .flatMap(e -> classroomSubjectRepository.findByClassroomId(e.getClassroom().getId()).stream())
                .map(allocation -> allocation.getSubject().getId()).collect(java.util.stream.Collectors.toSet());
        return subjects.stream().filter(subject -> subjectIds.contains(subject.getId())).toList();
    }

    public List<Attendance> visibleAttendance(List<Attendance> records, Authentication authentication) {
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId == null) return records;
        return records.stream().filter(record -> record.getEnrollment().getStudent().getId().equals(ownStudentId)).toList();
    }

    public List<ExamMark> visibleMarks(List<ExamMark> records, Authentication authentication) {
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId == null) return records;
        return records.stream().filter(record -> record.getEnrollment().getStudent().getId().equals(ownStudentId)).toList();
    }

    public List<LibraryMember> visibleLibraryMembers(List<LibraryMember> members, Authentication authentication) {
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId == null) return members;
        return members.stream().filter(member -> member.getStudent() != null
                && member.getStudent().getId().equals(ownStudentId)).toList();
    }

    public List<BookLending> visibleBookLendings(List<BookLending> lendings, Authentication authentication) {
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId == null) return lendings;
        return lendings.stream().filter(lending -> lending.getLibraryMember().getStudent() != null
                && lending.getLibraryMember().getStudent().getId().equals(ownStudentId)).toList();
    }

    public boolean studentVisible(Integer requestedId, Authentication authentication) {
        if (privileged(authentication) || authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"))) return true;
        return studentRepository.findById(requestedId)
                .map(student -> !visibleStudents(List.of(student), authentication).isEmpty()).orElse(false);
    }

    public boolean guardianVisible(Integer requestedId, Authentication authentication) {
        if (privileged(authentication)) return true;
        return visibleStudents(studentRepository.findAll(), authentication).stream()
                .map(Student::getGuardian).filter(java.util.Objects::nonNull)
                .anyMatch(guardian -> guardian.getId().equals(requestedId));
    }

    public boolean subjectVisible(Integer requestedId, Authentication authentication) {
        if (studentId(authentication) == null) return true;
        return enrollmentRepository.findByStudentId(studentId(authentication)).stream()
                .flatMap(enrollment -> classroomSubjectRepository.findByClassroomId(enrollment.getClassroom().getId()).stream())
                .anyMatch(allocation -> allocation.getSubject().getId().equals(requestedId));
    }

    public List<com.pirivena_project.pirivena.modal.ClassroomSubject> visibleClassroomSubjects(
            List<com.pirivena_project.pirivena.modal.ClassroomSubject> allocations, Authentication authentication) {
        if (privileged(authentication)) return allocations;
        Integer ownStudentId = studentId(authentication);
        if (ownStudentId != null) {
            var classroomIds = enrollmentRepository.findByStudentId(ownStudentId).stream()
                    .map(enrollment -> enrollment.getClassroom().getId()).collect(java.util.stream.Collectors.toSet());
            return allocations.stream().filter(allocation -> classroomIds.contains(allocation.getClassroom().getId())).toList();
        }
        Integer employeeId = employeeId(authentication);
        if (employeeId == null) return List.of();
        return allocations.stream().filter(allocation ->
                allocation.getTeacher() != null && allocation.getTeacher().getId().equals(employeeId)
                        || allocation.getClassroom().getClassTeacher() != null
                        && allocation.getClassroom().getClassTeacher().getId().equals(employeeId)).toList();
    }
}
