package com.university.university_events.core.service;

import org.springframework.stereotype.Service;

@Service
public abstract class AbstractService<T> {
    protected void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    protected abstract void validate(T entity, boolean uniqueCheck);
}
