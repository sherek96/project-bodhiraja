package com.pirivena_project.pirivena.repository;
import com.pirivena_project.pirivena.modal.LibraryMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Integer> {
    boolean existsByMembershipNo(String membershipNo);
    boolean existsByStudentId(Integer studentId);
    boolean existsByEmployeeId(Integer employeeId);

    @Query("select max(m.membershipNo) from LibraryMember m where m.membershipNo like 'LIB%'")
    String findMaxMembershipNo();
}
