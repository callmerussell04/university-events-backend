package com.university.university_events.surveys.model;

import java.util.ArrayList;
import java.util.List;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.faculties.model.FacultyEntity;
import com.university.university_events.surveys.questions.model.QuestionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "surveys")
public class SurveyEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();

    public SurveyEntity(String name, FacultyEntity faculty) {
        this.name = name;
    }
}