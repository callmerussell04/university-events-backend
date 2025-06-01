package com.university.university_events.faculties.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.faculties.model.FacultyEntity;


public interface FacultyRepository extends CrudRepository<FacultyEntity, Long>, PagingAndSortingRepository<FacultyEntity, Long> {}
