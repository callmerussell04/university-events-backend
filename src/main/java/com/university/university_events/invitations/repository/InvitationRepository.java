package com.university.university_events.invitations.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.invitations.model.InvitationEntity;


public interface InvitationRepository extends CrudRepository<InvitationEntity, Long>, PagingAndSortingRepository<InvitationEntity, Long> {
    List<InvitationEntity> findByEventId(long eventId);

    Page<InvitationEntity> findByEventId(long eventId, Pageable pageable);
}
