package com.university.university_events.invitations.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.invitations.repository.InvitationRepository;

@Service
public class InvitationService {
    private final InvitationRepository repository;

    public InvitationService(InvitationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InvitationEntity> getAll() {
        return StreamSupport.stream(repository.findAll(Sort.by("id")).spliterator(), false).toList();
    }

    
    @Transactional(readOnly = true)
    public Page<InvitationEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public InvitationEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(InvitationEntity.class, id));
    }

    @Transactional
    public InvitationEntity create(InvitationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        return repository.save(entity);
    }

    @Transactional
    public InvitationEntity update(Long id, InvitationEntity entity) {
        final InvitationEntity existsEntity = get(id);
        existsEntity.setStatus(entity.getStatus());
        return repository.save(existsEntity);
    }

    @Transactional
    public InvitationEntity delete(Long id) {
        final InvitationEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }
}
