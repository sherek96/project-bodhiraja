package com.pirivena_project.pirivena.controller;

import com.pirivena_project.pirivena.modal.Classroom;
import com.pirivena_project.pirivena.service.ClassroomService;
import com.pirivena_project.pirivena.security.AssignmentSecurity;
import com.pirivena_project.pirivena.service.ClassroomRosterService;
import com.pirivena_project.pirivena.dto.ClassroomRosterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final AssignmentSecurity assignmentSecurity;
    private final ClassroomRosterService classroomRosterService;

    // 1. Create or Update a Classroom container
    @PostMapping
    public ResponseEntity<Classroom> saveClassroom(@RequestBody Classroom classroom) {
        Classroom savedClassroom = classroomService.saveClassroom(classroom);
        return new ResponseEntity<>(savedClassroom, HttpStatus.CREATED);
    }

    // 2. Fetch all historical classrooms across all years
    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms(Authentication authentication) {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        return ResponseEntity.ok(assignmentSecurity.visibleClassrooms(classrooms, authentication));
    }

    // 3. Fetch classrooms for a specific academic year (Incredibly helpful for dashboard dropdown sorting)
    @GetMapping("/year/{academicYearId}")
    public ResponseEntity<List<Classroom>> getClassroomsByYear(@PathVariable Integer academicYearId, Authentication authentication) {
        List<Classroom> classrooms = classroomService.getClassroomsByAcademicYear(academicYearId);
        return ResponseEntity.ok(assignmentSecurity.visibleClassrooms(classrooms, authentication));
    }

    // 4. Fetch a specific classroom profile by primary key
    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@assignmentSecurity.classroomParticipant(#id, authentication)")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Integer id) {
        Classroom classroom = classroomService.getClassroomById(id);
        return ResponseEntity.ok(classroom);
    }

    @GetMapping("/{id}/roster")
    @org.springframework.security.access.prepost.PreAuthorize("@assignmentSecurity.classroomParticipant(#id, authentication)")
    public ResponseEntity<ClassroomRosterResponse> getClassroomRoster(@PathVariable Integer id) {
        return ResponseEntity.ok(classroomRosterService.getRoster(id));
    }

    // 5. Archive a planned classroom while retaining its record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassroom(@PathVariable Integer id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.ok("Success: Classroom has been archived without deleting historical data.");
    }
}
