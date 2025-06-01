package com.university.university_events.surveys.answers.model;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.surveys.options.model.OptionEntity;
import com.university.university_events.surveys.questions.model.QuestionEntity;
import com.university.university_events.users.model.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "answers")
public class AnswerEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "questionId")
    private QuestionEntity question;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity user;
    
    @ManyToOne
    @JoinColumn(name = "optionId")
    private OptionEntity option;

    @Column(length = 100)
    private String text;
}