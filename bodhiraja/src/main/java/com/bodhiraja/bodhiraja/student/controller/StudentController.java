package com.bodhiraja.bodhiraja.student.controller;

import com.bodhiraja.bodhiraja.academic.dao.GradeRepository;
import com.bodhiraja.bodhiraja.guardian.dao.GuardianRepository;
import com.bodhiraja.bodhiraja.student.Student;
import com.bodhiraja.bodhiraja.student.StudentStatus;
import com.bodhiraja.bodhiraja.student.dao.StudentRepository;
import com.bodhiraja.bodhiraja.student.dao.StudentStatusRepository;
import com.bodhiraja.bodhiraja.student.dao.StudentTypeRepository;
import com.bodhiraja.bodhiraja.student.StudentType;
import com.bodhiraja.bodhiraja.user.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/student")
@CrossOrigin
public class StudentController {

    // --- 1. Student Profiles (Personal Details) ---
    @Autowired
    private StudentRepository studentDao;
    // --- 2. Student Status (Active, Left School, etc.) ---
    @Autowired
    private StudentStatusRepository studentStatusDao;


    @GetMapping(value = "/all", produces = "application/json")
    public List<Student> getAllStudents() {
        // OLD: return studentDao.findAll();
        // NEW: Only get students where status is "Active"
        return studentDao.findByStudentStatus_Name("Active");
    }

    // --- 2. SOFT DELETE (The Magic Trick) ---
    @DeleteMapping(value = "/delete/{id}")
    public String deleteStudent(@PathVariable Integer id) {

        // A. Check if Student exists
        Optional<Student> studentOptional = studentDao.findById(id);
        if (!studentOptional.isPresent()) {
            return "Error: Student not found";
        }
        Student student = studentOptional.get();

        // B. Get the "Deleted" status badge
        // MAKE SURE your Database has a row in 'student_status' named "Deleted" (Case Sensitive!)
        StudentStatus deletedStatus = studentStatusDao.findByName("Deleted");

        if (deletedStatus == null) {
            return "Error: 'Deleted' status not found in Database. Please add it to student_status table.";
        }

        // C. Swap the badge
        student.setStudentStatus(deletedStatus);

        // D. Save the update
        studentDao.save(student);

        return "Success: Student marked as Deleted";
    }

    // --- 3. ADD NEW STUDENT (The POST Method) ---
    @PostMapping(value = "/add") // This creates the URL: http://localhost:8080/student/add
    public String addStudent(@RequestBody Student student) {

        try {
            studentDao.save(student);
            return "Saved Successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // --- 4. UPDATE STUDENT (The PUT Method) ---
    @PutMapping(value = "/update")
    public String updateStudent(@RequestBody Student student) {
        // 1. Check if ID exists (Crucial for Update)
        if (student.getId() == null) {
            return "Error: Student ID is missing. Cannot update.";
        }

        // 2. Check if the student actually exists in DB
        Optional<Student> existingRecord = studentDao.findById(student.getId());
        if (!existingRecord.isPresent()) {
            return "Error: Student with ID " + student.getId() + " does not exist.";
        }

        // 3. PRESERVE CRITICAL DATA (The Safety Step)
        // If the frontend didn't send the User or AddDate, we copy it from the database

        try {
            // 4. Perform the Update
            studentDao.save(student);
            return "Updated Successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // --- 3. HELPER: Get Statuses (For dropdowns later) ---
    @GetMapping(value = "/status/all", produces = "application/json")
    public List<StudentStatus> getAllStatuses() {
        return studentStatusDao.findAll();
    }
}