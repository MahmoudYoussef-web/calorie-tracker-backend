package com.caloriestracker.system.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Goal {

    LOSE(-500),
    MAINTAIN(0),
    GAIN(500);

    private final int calorieOffset;

    Goal(int calorieOffset) {
        this.calorieOffset = calorieOffset;
    }

    public int getCalorieOffset() {
        return calorieOffset;
    }


    @JsonValue
    public String getLabel() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Goal fromValue(String value) {
        return Goal.valueOf(value.toUpperCase());
    }
}

