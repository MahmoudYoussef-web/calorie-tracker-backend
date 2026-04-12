package com.caloriestracker.system.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {

    MALE(5),
    FEMALE(-161);

    private final int bmrOffset;

    Gender(int bmrOffset) {
        this.bmrOffset = bmrOffset;
    }

    public int getBmrOffset() {
        return bmrOffset;
    }

    @JsonValue
    public String getLabel() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        return Gender.valueOf(value.toUpperCase());
    }
}
