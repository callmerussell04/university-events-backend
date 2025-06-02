package com.university.university_events.surveys.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.university.university_events.surveys.questions.api.QuestionDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    private List<QuestionDto> questions;
}