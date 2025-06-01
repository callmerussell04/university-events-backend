package com.university.university_events.users.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.users.model.UserEntity;


public interface UserRepository extends CrudRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {}
