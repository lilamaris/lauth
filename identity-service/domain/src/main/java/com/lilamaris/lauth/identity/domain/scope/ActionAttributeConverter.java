package com.lilamaris.lauth.identity.domain.scope;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ActionAttributeConverter implements AttributeConverter<Action, String> {
    @Override
    public String convertToDatabaseColumn(Action attribute) {
        return attribute == null ? null : attribute.canonicalName();
    }

    @Override
    public Action convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        return Action.from(dbData);
    }
}
