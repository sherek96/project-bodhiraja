package com.pirivena_project.pirivena.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
// FIXED: Swapped out the .reactive version for the standard Servlet version
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Match your Vite React development port exactly
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        // FIXED: Using standard Servlet target container mapping
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // FIXED: Explicitly bind our CORS configuration  rules into the chain
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/users/me", "/api/users/profile/update").authenticated()
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "LIBRARIAN")
                        .requestMatchers("/api/employees/**").hasAnyRole("ADMIN", "PRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER", "LIBRARIAN")
                        .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers("/api/student-admissions/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/guardians/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers("/api/guardians/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/academic-years/**", "/api/subjects/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER", "STUDENT")
                        .requestMatchers("/api/academic-years/**", "/api/subjects/**", "/api/enrollments/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/classrooms/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/classroom-subjects/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER")
                        .requestMatchers("/api/classrooms/**", "/api/classroom-subjects/**", "/api/promotions/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers("/api/attendances/**", "/api/exam-marks/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER")
                        .requestMatchers("/api/report-cards/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/funding-pools/**", "/api/income-categories/**").hasAnyRole("ADMIN", "PRINCIPAL", "LIBRARIAN")
                        .requestMatchers("/api/funding-pools/**", "/api/incomes/**", "/api/expenses/**", "/api/income-categories/**", "/api/expense-categories/**").hasAnyRole("ADMIN", "PRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/books/**", "/api/book-categories/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER", "LIBRARIAN", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/library-members/**", "/api/book-lendings/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "LIBRARIAN")
                        .requestMatchers("/api/books/**", "/api/book-categories/**", "/api/library-members/**", "/api/book-lendings/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/events/**", "/api/event-categories/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL", "TEACHER", "LIBRARIAN", "STUDENT")
                        .requestMatchers("/api/events/**", "/api/event-categories/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .requestMatchers(HttpMethod.GET, "/api/enums/**", "/api/designations/**").authenticated()
                        .requestMatchers("/api/designations/**").hasAnyRole("ADMIN", "PRINCIPAL", "VICEPRINCIPAL")
                        .anyRequest().denyAll()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add our watchdog filter right before the default username/password checker filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
