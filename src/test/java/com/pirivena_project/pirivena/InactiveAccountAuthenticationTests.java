// Tests backend rules directly, without using the frontend.
package com.pirivena_project.pirivena;

// Purpose: Verifies that deactivated user accounts cannot authenticate.

import com.pirivena_project.pirivena.controller.AuthController;
import com.pirivena_project.pirivena.dto.LoginRequest;
import com.pirivena_project.pirivena.repository.UserRepository;
import com.pirivena_project.pirivena.security.CustomUserDetailsService;
import com.pirivena_project.pirivena.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InactiveAccountAuthenticationTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void inactiveDatabaseUserProducesDisabledUserDetails() {
        com.pirivena_project.pirivena.model.User user = new com.pirivena_project.pirivena.model.User();
        user.setUsername("ishan");
        user.setPassword("encoded-password");
        user.setIsActive(false);
        user.setRoles(new HashSet<>());
        when(userRepository.findByUsername("ishan")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("ishan");

        assertFalse(details.isEnabled());
    }

    @Test
    void loginEndpointRejectsInactiveAccountBeforeCheckingPassword() {
        CustomUserDetailsService detailsService = mock(CustomUserDetailsService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserDetails inactiveUser = User.withUsername("ishan")
                .password("encoded-password")
                .authorities("ROLE_TEACHER")
                .disabled(true)
                .build();
        when(detailsService.loadUserByUsername("ishan")).thenReturn(inactiveUser);

        LoginRequest request = new LoginRequest();
        request.setUsername("ishan");
        request.setPassword("correct-password");
        ResponseEntity<?> response = new AuthController(detailsService, passwordEncoder, jwtUtil).login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("This account is inactive. Contact an administrator.", response.getBody());
        verifyNoInteractions(passwordEncoder, jwtUtil);
    }
}
