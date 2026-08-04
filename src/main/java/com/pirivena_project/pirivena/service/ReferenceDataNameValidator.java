package com.pirivena_project.pirivena.service;

import org.springframework.stereotype.Component;

@Component
public class ReferenceDataNameValidator {

    public String requiredName(String value, String label, int maximumLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " name is required.");
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(label + " name must not exceed " + maximumLength + " characters.");
        }
        return normalized;
    }

    public String optionalText(String value, int maximumLength, String label) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(label + " must not exceed " + maximumLength + " characters.");
        }
        return normalized;
    }
}
