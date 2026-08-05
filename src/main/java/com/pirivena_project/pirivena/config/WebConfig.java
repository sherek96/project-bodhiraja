package com.pirivena_project.pirivena.config;

// Purpose: Makes uploaded profile pictures available through HTTP URLs.

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload.employee-dir:uploads/employees}")
    private String employeeUploadDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(employeeUploadDirectory).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/employees/**")
                .addResourceLocations(location);
        registry.addResourceHandler("/uploads/students/**")
                .addResourceLocations(Paths.get("uploads/students").toAbsolutePath().normalize().toUri().toString());
        registry.addResourceHandler("/uploads/guardians/**")
                .addResourceLocations(Paths.get("uploads/guardians").toAbsolutePath().normalize().toUri().toString());
    }
}
