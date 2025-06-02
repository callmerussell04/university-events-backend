package com.university.university_events.surveys.options.model;


import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.surveys.questions.model.QuestionEntity;

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
@Table(name = "options")
public class OptionEntity extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String text;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private QuestionEntity question;
}