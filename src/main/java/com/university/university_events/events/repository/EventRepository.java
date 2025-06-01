package com.university.university_events.events.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.events.model.EventEntity;


public interface EventRepository extends CrudRepository<EventEntity, Long>, PagingAndSortingRepository<EventEntity, Long> {}
