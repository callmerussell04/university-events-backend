package com.university.university_events.users.repository;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.model.UserRole;


public interface UserRepository extends CrudRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    List<UserEntity> findByRole(UserRole role);

    Page<UserEntity> findByRole(UserRole role, Pageable pageable);
}
