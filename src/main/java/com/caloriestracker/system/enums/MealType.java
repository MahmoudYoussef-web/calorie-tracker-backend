package com.caloriestracker.system.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MealType {
    BREAKFAST(1),
    LUNCH(2),
    DINNER(3),
    SNACK(4);

    private final int order;

    MealType(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    @JsonValue
    public String getLabel() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static MealType fromValue(String value) {
        return MealType .valueOf(value.toUpperCase());
    }
}


