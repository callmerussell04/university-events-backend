package com.university.university_events.surveys.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.university.university_events.surveys.model.SurveyEntity;


public interface SurveyRepository extends CrudRepository<SurveyEntity, Long> {
    Optional<SurveyEntity> findByNameIgnoreCase(String name);
}
