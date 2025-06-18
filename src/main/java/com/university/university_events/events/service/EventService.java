package com.university.university_events.events.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.core.service.NotificationService;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.model.EventStatus;
import com.university.university_events.events.repository.EventRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EventService extends AbstractService<EventEntity> {
    private final EventRepository repository;
    private final NotificationService notificationService;

    public EventService(EventRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
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
    public List<EventEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<EventEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<EventEntity> getAll(String statusStr, Long locationId, Date startDate, Date endDate, String name, int page, int size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        EventStatus status = (statusStr != null) ? EventStatus.valueOf(statusStr.toUpperCase()) : null;
        return repository.findFilteredEvents(locationId, status, startDate, endDate, (name != null && !name.isBlank()) ? name.toLowerCase() : "", pageRequest);
    }

    @Transactional(readOnly = true)
    public EventEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(EventEntity.class, id));
    }

    @Transactional
    public EventEntity create(EventEntity entity) {
        validate(entity, true);
        return repository.save(entity);
    }

    public EventEntity update(Long id, EventEntity entity) {
        return repository.findById(id).map(existsEntity -> {
            EventStatus oldStatus = existsEntity.getStatus();

            existsEntity.setName(entity.getName());
            existsEntity.setStatus(entity.getStatus());
            existsEntity.setStartDateTime(entity.getStartDateTime());
            existsEntity.setEndDateTime(entity.getEndDateTime());
            existsEntity.setLocation(entity.getLocation());
            existsEntity.setOrganizer(entity.getOrganizer());

            EventEntity savedEvent = repository.save(existsEntity);

            if (!Objects.equals(oldStatus, savedEvent.getStatus()) &&
                (savedEvent.getStatus() == EventStatus.ACTIVE || savedEvent.getStatus() == EventStatus.CANCELED)) {
                notificationService.sendEventStatusChangeNotification(savedEvent);
            }
            return savedEvent;
        }).orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Transactional
    public EventEntity delete(Long id) {
        final EventEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(EventEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Event entity is null");
        }
        validateStringField(entity.getName(), "Event name");
        validateStringField(entity.getOrganizer(), "Organizer name");
        if (entity.getStatus() == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
        if (entity.getStartDateTime() == null) {
            throw new IllegalArgumentException("Start datetime must not be null");
        }
        if (entity.getEndDateTime() == null) {
            throw new IllegalArgumentException("End datetime must not be null");
        }
        if (entity.getEndDateTime().compareTo(entity.getStartDateTime()) <= 0) {
            throw new IllegalArgumentException("End datetime must be later that start datetime");
        }
        if (uniqueCheck) {
            if (repository.findByNameIgnoreCase(entity.getName()).isPresent()) {
                throw new IllegalArgumentException(
                        String.format("Event with name %s already exists", entity.getName())
                );
            }
        }
    }
    @Transactional(readOnly = true)
    public List<Object[]> getEventStatistics(Date startDate, Date endDate) {
        return repository.findEventStatisticsByPeriod(startDate, endDate);
    }
}
