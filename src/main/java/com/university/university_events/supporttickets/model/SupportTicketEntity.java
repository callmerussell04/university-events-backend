package com.university.university_events.supporttickets.model;

import com.university.university_events.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "supporttickets")
public class SupportTicketEntity extends BaseEntity {
    @Column( nullable = false)
    private String telegramChatId;

    @Column(length = 500, nullable = false)
    private String userMessage;

    @Column(length = 500)
    private String operatorReply;
}