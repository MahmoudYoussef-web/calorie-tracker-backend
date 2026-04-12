package com.caloriestracker.system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ImageStatus {
    PENDING,
    PROCESSING,
    DONE,
    FAILED,
    CANCELLED;


    @JsonValue
    public String getLabel() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static ImageStatus fromValue(String value) {
        return ImageStatus.valueOf(value.toUpperCase());
    }
    }
