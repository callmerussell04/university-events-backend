package com.university.university_events.invitations.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.invitations.model.InvitationEntity;


public interface InvitationRepository extends CrudRepository<InvitationEntity, Long>, PagingAndSortingRepository<InvitationEntity, Long> {}
