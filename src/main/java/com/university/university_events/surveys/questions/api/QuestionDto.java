package com.university.university_events.surveys.questions.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.university.university_events.surveys.options.api.OptionDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String text;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    @Min(1)
    private Long surveyId;

    private List<OptionDto> options;
}