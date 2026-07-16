package com.pirivena_project.pirivena.dto;

import com.pirivena_project.pirivena.enums.GuardianRelationship;

public record GuardianDeactivationRequest(
        Integer replacementGuardianId,
        GuardianRelationship replacementRelationship
) {}
