package com.pirivena_project.pirivena;

import com.pirivena_project.pirivena.enums.EmployeeStatus;
import com.pirivena_project.pirivena.modal.Employee;
import com.pirivena_project.pirivena.modal.User;
import com.pirivena_project.pirivena.repository.UserRepository;
import com.pirivena_project.pirivena.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeStatusSynchronizationTests {
    @Test
    void activeEmploymentReactivatesLinkedAccountAndTerminationDisablesIt() {
        UserRepository users = mock(UserRepository.class);
        EmployeeService service = new EmployeeService();
        ReflectionTestUtils.setField(service, "userRepository", users);
        Employee employee = new Employee(); employee.setId(7); employee.setStatus(EmployeeStatus.ACTIVE);
        User user = new User(); user.setIsActive(false);
        when(users.findByEmployeeId(7)).thenReturn(Optional.of(user));

        ReflectionTestUtils.invokeMethod(service, "synchronizeLinkedUserStatus", employee);
        assertTrue(user.getIsActive());

        employee.setStatus(EmployeeStatus.TERMINATED);
        ReflectionTestUtils.invokeMethod(service, "synchronizeLinkedUserStatus", employee);
        assertFalse(user.getIsActive());
        verify(users, times(2)).save(user);
    }
}
