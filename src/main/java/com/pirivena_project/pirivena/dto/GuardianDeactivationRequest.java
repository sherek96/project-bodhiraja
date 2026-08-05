package com.pirivena_project.pirivena.dto;

// Purpose: Carries guardian deactivation input from the frontend to the backend.

import com.pirivena_project.pirivena.enums.GuardianRelationship;

public record GuardianDeactivationRequest(
        Integer replacementGuardianId,
        GuardianRelationship replacementRelationship
) {}
