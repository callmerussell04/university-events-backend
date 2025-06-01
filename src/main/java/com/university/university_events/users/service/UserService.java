package com.university.university_events.users.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {
        return StreamSupport.stream(repository.findAll(Sort.by("id")).spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public UserEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));
    }

    @Transactional
    public UserEntity create(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        return repository.save(entity);
    }

    @Transactional
    public UserEntity update(Long id, UserEntity entity) {
        final UserEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setLogin(entity.getLogin());
        existsEntity.setPhoneNumber(entity.getPhoneNumber());
        existsEntity.setPassword(entity.getPassword());
        existsEntity.setRole(entity.getRole());
        existsEntity.setGroup(entity.getGroup());
        return repository.save(existsEntity);
    }

    @Transactional
    public UserEntity delete(Long id) {
        final UserEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }
}
