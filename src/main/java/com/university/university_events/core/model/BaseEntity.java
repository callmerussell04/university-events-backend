package com.university.university_events.core.model;

import com.university.university_events.core.configuration.Constants;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Constants.SEQUENCE_NAME)
    @SequenceGenerator(name = Constants.SEQUENCE_NAME, sequenceName = Constants.SEQUENCE_NAME, allocationSize = 1)
    protected Long id;

    protected BaseEntity() {
    }
}
