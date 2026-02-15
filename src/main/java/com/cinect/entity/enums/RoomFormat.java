package com.cinect.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoomFormat {
    _2D("2D"), _3D("3D"), IMAX("IMAX"), _4DX("4DX"), DOLBY("DOLBY");

    private final String value;

    RoomFormat(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static RoomFormat fromValue(String v) {
        for (RoomFormat f : values()) {
            if (f.value.equalsIgnoreCase(v)) return f;
        }
        throw new IllegalArgumentException("Unknown format: " + v);
    }
}
