package com.cinect.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoomFormat {
    _2D("2D"), _3D("3D"), IMAX("IMAX"), _4DX("4DX"), DOLBY("DOLBY");

    private final String value;

    RoomFormat(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    /** Deserialize JSON {@code "2D"}, {@code "IMAX"}, etc. (not only enum names like {@code _2D}). */
    @JsonCreator
    public static RoomFormat fromJson(String v) {
        if (v == null || v.isBlank()) return _2D;
        try {
            return fromValue(v.trim());
        } catch (IllegalArgumentException ex) {
            return _2D;
        }
    }

    public static RoomFormat fromValue(String v) {
        for (RoomFormat f : values()) {
            if (f.value.equalsIgnoreCase(v)) return f;
        }
        throw new IllegalArgumentException("Unknown format: " + v);
    }
}
