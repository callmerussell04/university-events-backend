package com.university.university_events.locations.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.locations.model.LocationEntity;
import com.university.university_events.locations.repository.LocationRepository;

@Service
public class LocationService {
    private final LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<LocationEntity> getAll() {
        return StreamSupport.stream(repository.findAll(Sort.by("id")).spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public LocationEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(LocationEntity.class, id));
    }

    @Transactional
    public LocationEntity create(LocationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        return repository.save(entity);
    }

    @Transactional
    public LocationEntity update(Long id, LocationEntity entity) {
        final LocationEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        return repository.save(existsEntity);
    }

    @Transactional
    public LocationEntity delete(Long id) {
        final LocationEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }
}
