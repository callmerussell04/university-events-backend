package com.university.university_events.supporttickets.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.university.university_events.supporttickets.model.SupportTicketEntity;


public interface SupportTicketRepository extends CrudRepository<SupportTicketEntity, Long>, PagingAndSortingRepository<SupportTicketEntity, Long> {
    Page<SupportTicketEntity> findByOperatorReplyIsNull(Pageable pageable);
}
