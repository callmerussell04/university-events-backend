package com.university.university_events.surveys.questions.model;

import java.util.ArrayList;
import java.util.List;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.surveys.model.SurveyEntity;
import com.university.university_events.users.model.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "questions")
public class QuestionEntity extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String text;

    @ManyToOne
    @JoinColumn(name = "surveyId")
    private SurveyEntity survey;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> users = new ArrayList<>();

    public QuestionEntity(String text, SurveyEntity survey) {
        this.text = text;
        this.survey = survey;
    }
}