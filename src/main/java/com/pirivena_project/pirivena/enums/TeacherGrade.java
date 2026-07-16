package com.pirivena_project.pirivena.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TeacherGrade {
    GRADE_1("Grade 1"),
    GRADE_2_I("Grade 2-I"),
    GRADE_2_II("Grade 2-II"),
    GRADE_3_I("Grade 3-I"),
    GRADE_3_II("Grade 3-II");

    private final String displayName;

    // The missing constructor that fixes your compiler error
    TeacherGrade(String displayName) {
        this.displayName = displayName;
    }

    // Jackson will automatically call this to get "Grade 1", "Grade 2-I", etc.
    public String getDisplayName() {
        return displayName;
    }

    // Jackson will automatically call this to get "GRADE_1", "GRADE_2_I", etc.
    public String getName() {
        return this.name();
    }
}