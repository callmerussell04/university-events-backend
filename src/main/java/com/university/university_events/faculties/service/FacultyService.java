package com.university.university_events.faculties.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.faculties.model.FacultyEntity;
import com.university.university_events.faculties.repository.FacultyRepository;

@Service
public class FacultyService extends AbstractService<FacultyEntity> {
    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FacultyEntity> getAll() {
        return StreamSupport.stream(repository.findAll(Sort.by("id")).spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public FacultyEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(FacultyEntity.class, id));
    }

    @Transactional
    public FacultyEntity create(FacultyEntity entity) {
        validate(entity, true);
        return repository.save(entity);
    }

    @Transactional
    public FacultyEntity update(Long id, FacultyEntity entity) {
        validate(entity, false);
        final FacultyEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        return repository.save(existsEntity);
    }

    @Transactional
    public FacultyEntity delete(Long id) {
        final FacultyEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(FacultyEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Faculty entity is null");
        }
        validateStringField(entity.getName(), "Faculty name");
        if (uniqueCheck) {
            if (repository.findByNameIgnoreCase(entity.getName()).isPresent()) {
                throw new IllegalArgumentException(
                        String.format("Faculty with name %s already exists", entity.getName())
                );
            }
        }
    }
}
