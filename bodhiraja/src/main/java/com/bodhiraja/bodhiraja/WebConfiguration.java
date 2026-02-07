package com.bodhiraja.bodhiraja;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    // This method gives permission to the Frontend
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // "/**" means ALL URLs are open
        // allowedMethods("*") means ALL actions (GET, POST, DELETE) are allowed
        registry.addMapping("/**").allowedMethods("*");
    }
}