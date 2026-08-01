package com.pirivena_project.pirivena.repository;

import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.modal.Student;
import com.pirivena_project.pirivena.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    // Username checks
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Integer id);

    // Single active seat role checks
    boolean existsByRolesNameAndIsActiveTrue(String roleName);
    boolean existsByRolesNameAndIsActiveTrueAndIdNot(String roleName, Integer id);

    // One-to-One relationship identity linkage checks
    boolean existsByEmployeeId(Integer employeeId);
    boolean existsByEmployeeIdAndIdNot(Integer employeeId, Integer userId);

    boolean existsByStudentId(Integer studentId);
    boolean existsByStudentIdAndIdNot(Integer studentId, Integer userId);

    // Custom Query: Pull all employees who DO NOT possess a user credentials link
    @Query("SELECT e FROM Employee e WHERE e.id NOT IN (SELECT u.employee.id FROM User u WHERE u.employee IS NOT NULL)")
    List<Employee> findEmployeesWithoutAccount();

    // Custom Query: Pull all students who DO NOT possess a user credentials link
    @Query("SELECT s FROM Student s WHERE s.id NOT IN (SELECT u.student.id FROM User u WHERE u.student IS NOT NULL)")
    List<Student> findStudentsWithoutAccount();

    Optional<User> findByUsername(String username);
    Optional<User> findByEmployeeId(Integer employeeId);
    Optional<User> findByStudentId(Integer studentId);
}
