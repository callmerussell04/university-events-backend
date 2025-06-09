package com.university.university_events.invitations.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.invitations.repository.InvitationRepository;

@Service
public class InvitationService extends AbstractService<InvitationEntity> {
    private final InvitationRepository repository;

    public InvitationService(InvitationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InvitationEntity> getAll(Long eventId) {
        if (eventId <= 0L) {
            return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        } else {
            return repository.findByEventId(eventId);
        }
    }

    
    @Transactional(readOnly = true)
    public Page<InvitationEntity> getAll(Long eventId, int page, int size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        if (eventId <= 0L) {
            return repository.findAll(PageRequest.of(page, size));
        } else {
            return repository.findByEventId(eventId, pageRequest);
        }
    }

    @Transactional(readOnly = true)
    public InvitationEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(InvitationEntity.class, id));
    }

    @Transactional
    public InvitationEntity create(InvitationEntity entity) {
        validate(entity, true);
        if(repository.existsByUserAndEvent(entity.getUser(), entity.getEvent())) 
            throw new IllegalArgumentException("this invitation already exists");
        return repository.save(entity);
    }

    @Transactional
    public void create(List<InvitationEntity> entityList) {
        entityList.removeIf(invitation -> {
            validate(invitation, true);
            return repository.existsByUserAndEvent(invitation.getUser(), invitation.getEvent());
        });
        repository.saveAll(entityList);
    }

    @Transactional
    public InvitationEntity update(Long id, InvitationEntity entity) {
        validate(entity, false);
        final InvitationEntity existsEntity = get(id);
        existsEntity.setUser(entity.getUser());
        existsEntity.setEvent(entity.getEvent());
        existsEntity.setStatus(entity.getStatus());
        return repository.save(existsEntity);
    }

    @Transactional
    public InvitationEntity delete(Long id) {
        final InvitationEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<InvitationEntity> getInvitationsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return repository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<InvitationEntity> getInvitationsByUserId(Long userId, int page, int size) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        final Pageable pageRequest = PageRequest.of(page, size);
        return repository.findByUserId(userId, pageRequest);
    }

    @Override
    protected void validate(InvitationEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Invitation entity is null");
        }
        if (entity.getUser() == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (entity.getEvent() == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        if (entity.getStatus() == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
    }
}
