package com.university.university_events.surveys.api;

import java.util.List;

import com.university.university_events.surveys.answers.api.AnswerDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubmitSurveyDto {
    @NotNull
    @Min(1)
    private Long userId;

    private List<AnswerDto> answers;
}