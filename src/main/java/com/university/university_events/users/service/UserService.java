package com.university.university_events.users.service;

import java.util.List;
import java.util.stream.StreamSupport;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.configuration.Constants;
import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.model.UserRole;
import com.university.university_events.users.repository.UserRepository;

@Service
public class UserService extends AbstractService<UserEntity> {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAll(String role) {
        if (role == null || role.isBlank()) {
            return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        } else {
            return repository.findByRole(UserRole.valueOf(role));
        }
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> getAll(String role, int page, int size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        if (role == null || role.isBlank()) {
            return repository.findAll(PageRequest.of(page, size));
        } else {
            return repository.findByRole(UserRole.valueOf(role), pageRequest);
        }
    }

    @Transactional(readOnly = true)
    public UserEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));
    }

    @Transactional
    public UserEntity create(UserEntity entity) {
        validate(entity, true);
        return repository.save(entity);
    }

    @Transactional
    public UserEntity update(Long id, UserEntity entity) {
        validate(entity, false);
        final UserEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setEmail(entity.getEmail());
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

    @Override
    protected void validate(UserEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("User entity is null");
        }
        validateStringField(entity.getName(), "User name");
        validateStringField(entity.getLogin(), "User login");
        validateStringField(entity.getEmail(), "User email");
        try {
            InternetAddress emailAddr = new InternetAddress(entity.getEmail());
            emailAddr.validate();
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Email has invalid format: " + entity.getEmail());
        }
        if (!entity.getPassword().matches(Constants.PASSWORD_PATTERN)) {
            throw new IllegalArgumentException("Password has invalid format: " + entity.getPassword());
        }
        validateStringField(entity.getPhoneNumber(), "User phone number");
        entity.setPhoneNumber(normalizePhoneNumber(entity.getPhoneNumber()));
        if (uniqueCheck) {
            if (repository.findByEmailIgnoreCase(entity.getName()).isPresent()) {
                throw new IllegalArgumentException(
                        String.format("User with name %s already exists", entity.getName())
                );
            }
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches(Constants.PHONE_PATTERN)) {
            throw new IllegalArgumentException("Phone number has invalid format: " + phoneNumber);
        }
        String cleaned = phoneNumber.replaceAll("[\\s\\-()]", "");
        if (cleaned.startsWith("8")) {
            cleaned = "+7" + cleaned.substring(1);
        } else if (!cleaned.startsWith("+")) {
            throw new IllegalArgumentException("Phone number must start with +country code or 8");
        }
        return cleaned;
    }
}
