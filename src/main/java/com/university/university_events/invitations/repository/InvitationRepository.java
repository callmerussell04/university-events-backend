package com.university.university_events.invitations.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.events.model.EventEntity;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.users.model.UserEntity;


public interface InvitationRepository extends CrudRepository<InvitationEntity, Long>, PagingAndSortingRepository<InvitationEntity, Long> {
    List<InvitationEntity> findByEventId(long eventId);

    Page<InvitationEntity> findByEventId(long eventId, Pageable pageable);

    List<InvitationEntity> findByUserId(long userId);

    Page<InvitationEntity> findByUserId(long userId, Pageable pageable);
    
    boolean existsByUserAndEvent(UserEntity user, EventEntity event);
}
