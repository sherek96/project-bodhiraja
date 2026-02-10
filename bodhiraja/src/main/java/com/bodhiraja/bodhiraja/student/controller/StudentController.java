package com.bodhiraja.bodhiraja.student.controller;

import com.bodhiraja.bodhiraja.academic.dao.GradeRepository;
import com.bodhiraja.bodhiraja.guardian.dao.GuardianRepository;
import com.bodhiraja.bodhiraja.student.Student;
import com.bodhiraja.bodhiraja.student.StudentStatus;
import com.bodhiraja.bodhiraja.student.dao.StudentRepository;
import com.bodhiraja.bodhiraja.student.dao.StudentStatusRepository;
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

    @GetMapping(value = "/all", produces = "application/json")
    public List<Student> getAllStudents() {
        // OLD: return studentDao.findAll();
        // NEW: Only get students where status is "Active"
        return studentDao.findByStudentStatus_Name("Active");
    }
    // --- 2. Student Status (Active, Left School, etc.) ---
    @Autowired
    private StudentStatusRepository studentStatusDao;
    @Autowired
    private GradeRepository gradeDao;
    @Autowired
    private GuardianRepository guardianDao;
    @Autowired
    private UserRepository userDao;

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

        student.setGrade(gradeDao.findById(1).orElse(null));
        student.setStudentStatus(studentStatusDao.findById(1).orElse(null));
        student.setGuardian(guardianDao.findById(1).orElse(null));
        student.setUser(userDao.findById(2).orElse(null));

        // 1. We receive the "student" object from React (JSON format)
        // 2. We save it to the database
        try {
            studentDao.save(student);
            return "Saved Successfully";
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