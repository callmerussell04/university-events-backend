package com.university.university_events.surveys.questions.repository;

import org.springframework.data.repository.CrudRepository;

import com.university.university_events.surveys.questions.model.QuestionEntity;


public interface QuestionRepository extends CrudRepository<QuestionEntity, Long> {}
