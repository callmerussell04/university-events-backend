package com.university.university_events.events.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.model.EventStatus;
import com.university.university_events.events.repository.EventRepository;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public boolean isValidEventStatus(String status) {
        try {
            EventStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<EventEntity> getAll(String status, Long locationId) {
        if (!isValidEventStatus(status) && locationId <= 0L) {
            return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        } else if (locationId <= 0L) {
            EventStatus statusEnum = EventStatus.valueOf(status);
            return repository.findByStatus(statusEnum);
        } else if (!isValidEventStatus(status)) {
            return repository.findByLocationId(locationId);
        } else {
            EventStatus statusEnum = EventStatus.valueOf(status);
            return repository.findByStatusAndLocationId(statusEnum, locationId);
        }
    }

    @Transactional(readOnly = true)
    public Page<EventEntity> getAll(String status, Long locationId, int page, int size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        if (!isValidEventStatus(status) && locationId <= 0L) {
            return repository.findAll(PageRequest.of(page, size));
        } else if (locationId <= 0L) {
            EventStatus statusEnum = EventStatus.valueOf(status);
            return repository.findByStatus(statusEnum, pageRequest);
        } else if (!isValidEventStatus(status)) {
            return repository.findByLocationId(locationId, pageRequest);
        } else {
            EventStatus statusEnum = EventStatus.valueOf(status);
            return repository.findByStatusAndLocationId(statusEnum, locationId, pageRequest);
        }
    }

    @Transactional(readOnly = true)
    public EventEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(EventEntity.class, id));
    }

    @Transactional
    public EventEntity create(EventEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        return repository.save(entity);
    }

    @Transactional
    public EventEntity update(Long id, EventEntity entity) {
        final EventEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setStatus(entity.getStatus());
        existsEntity.setDateTime(entity.getDateTime());
        existsEntity.setLocation(entity.getLocation());
        existsEntity.setOrganizer(entity.getOrganizer());
        return repository.save(existsEntity);
    }

    @Transactional
    public EventEntity delete(Long id) {
        final EventEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }
}
