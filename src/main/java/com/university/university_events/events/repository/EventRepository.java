package com.university.university_events.events.repository;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.model.EventStatus;


public interface EventRepository extends CrudRepository<EventEntity, Long>, PagingAndSortingRepository<EventEntity, Long> {
    List<EventEntity> findByLocationId(long locationId);

    Page<EventEntity> findByLocationId(long locationId, Pageable pageable);

    List<EventEntity> findByStatus(EventStatus status);

    Page<EventEntity> findByStatus(EventStatus status, Pageable pageable);

    List<EventEntity> findByStatusAndLocationId(EventStatus status, long locationId);

    Page<EventEntity> findByStatusAndLocationId(EventStatus status, long locationId, Pageable pageable);
}
