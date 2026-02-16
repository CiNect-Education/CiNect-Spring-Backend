package com.cinect.entity.converter;

import com.cinect.entity.enums.RoomFormat;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoomFormatConverter implements AttributeConverter<RoomFormat, String> {

    @Override
    public String convertToDatabaseColumn(RoomFormat attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public RoomFormat convertToEntityAttribute(String dbData) {
        return dbData != null ? RoomFormat.fromValue(dbData) : null;
    }
}
