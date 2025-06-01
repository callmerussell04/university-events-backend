package com.university.university_events.groups.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.groups.model.GroupEntity;


public interface GroupRepository extends CrudRepository<GroupEntity, Long>, PagingAndSortingRepository<GroupEntity, Long> {}
