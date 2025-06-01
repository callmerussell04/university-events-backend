package com.university.university_events.locations.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.locations.model.LocationEntity;


public interface LocationRepository extends CrudRepository<LocationEntity, Long>, PagingAndSortingRepository<LocationEntity, Long> {}
