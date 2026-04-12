package com.caloriestracker.system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityLevel {

    SEDENTARY(1.2),
    LIGHT(1.375),
    MODERATE(1.55),
    ACTIVE(1.725),
    VERY_ACTIVE(1.9);

    private final double factor;

    ActivityLevel(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }


    @JsonValue
    public String getLabel() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static ActivityLevel fromValue(String value) {
        return ActivityLevel.valueOf(value.toUpperCase());
    }
}