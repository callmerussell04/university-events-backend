package com.university.university_events.groups.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.groups.model.GroupEntity;


public interface GroupRepository extends CrudRepository<GroupEntity, Long>, PagingAndSortingRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByNameIgnoreCase(String name);
    List<GroupEntity> findByFacultyIdAndCourse(Long facultyId, int course);
}
