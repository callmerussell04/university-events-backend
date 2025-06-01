package com.university.university_events.groups.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.groups.model.GroupEntity;
import com.university.university_events.groups.repository.GroupRepository;

@Service
public class GroupService extends AbstractService<GroupEntity> {
    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GroupEntity> getAll() {
        return StreamSupport.stream(repository.findAll(Sort.by("id")).spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<GroupEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public GroupEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(GroupEntity.class, id));
    }

    @Transactional
    public GroupEntity create(GroupEntity entity) {
        validate(entity, true);
        return repository.save(entity);
    }

    @Transactional
    public GroupEntity update(Long id, GroupEntity entity) {
        validate(entity, false);
        final GroupEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setFaculty(entity.getFaculty());
        return repository.save(existsEntity);
    }

    @Transactional
    public GroupEntity delete(Long id) {
        final GroupEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(GroupEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Group entity is null");
        }
        validateStringField(entity.getName(), "Group name");
        if (entity.getFaculty() == null) {
            throw new IllegalArgumentException("Faculty must not be null");
        }
        if (uniqueCheck) {
            if (repository.findByNameIgnoreCase(entity.getName()).isPresent()) {
                throw new IllegalArgumentException(
                        String.format("Group with name %s already exists", entity.getName())
                );
            }
        }
    }
}
