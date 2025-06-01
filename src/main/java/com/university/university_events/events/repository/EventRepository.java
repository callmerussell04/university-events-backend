package com.university.university_events.events.repository;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.model.EventStatus;


public interface EventRepository extends CrudRepository<EventEntity, Long>, PagingAndSortingRepository<EventEntity, Long> {
    Optional<EventEntity> findByNameIgnoreCase(String name);
    
    @Query("""
    SELECT e FROM EventEntity e
    WHERE (:locationId IS NULL OR e.location.id = :locationId)
      AND (:status IS NULL OR e.status = :status)
      AND (COALESCE(:startDate, null) IS NULL OR e.startDateTime >= :startDate)
      AND (COALESCE(:endDate, null) IS NULL OR e.startDateTime <= :endDate)
      AND (:name IS NULL OR LOWER(e.name) LIKE CONCAT('%', :name, '%'))
    """)
    Page<EventEntity> findFilteredEvents(
        @Param("locationId") Long locationId,
        @Param("status") EventStatus status,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        @Param("name") String name,
        Pageable pageable
    );
}
