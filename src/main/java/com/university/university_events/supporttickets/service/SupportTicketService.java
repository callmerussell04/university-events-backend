package com.university.university_events.supporttickets.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.core.service.NotificationService;
import com.university.university_events.supporttickets.model.SupportTicketEntity;
import com.university.university_events.supporttickets.repository.SupportTicketRepository;

@Service
public class SupportTicketService extends AbstractService<SupportTicketEntity> {
    private final SupportTicketRepository repository;
    private final NotificationService notificationService;

    public SupportTicketService(SupportTicketRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<SupportTicketEntity> getAll() {
        return StreamSupport.stream(repository.findAll(Sort.by("id")).spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<SupportTicketEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public SupportTicketEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(SupportTicketEntity.class, id));
    }

    @Transactional(readOnly = true)
    public Page<SupportTicketEntity> getUnansweredTickets(int page, int size) {
        return repository.findByOperatorReplyIsNull(PageRequest.of(page, size)); 
    }

    @Transactional
    public SupportTicketEntity create(SupportTicketEntity entity) {
        validate(entity, false);
        return repository.save(entity);
    }

    @Transactional
    public SupportTicketEntity update(Long id, SupportTicketEntity entity) {
        validate(entity, false);
        final SupportTicketEntity existsEntity = get(id);
        existsEntity.setOperatorReply(entity.getOperatorReply());
        notificationService.sendTelegramMessage(
            existsEntity.getTelegramChatId(),
            "✉️ *Ответ техподдержки:*\n\n" + existsEntity.getOperatorReply()
        );
        return repository.save(existsEntity);
    }

    @Override
    protected void validate(SupportTicketEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("SupportTicket entity is null");
        }
        validateStringField(entity.getUserMessage(), "SupportTicket user message");
        if (entity.getTelegramChatId() == null) {
            throw new IllegalArgumentException("Telegram chat id must not be null");
        }
    }
}
