package com.university.university_events.surveys.api;

import java.util.List;

import com.university.university_events.surveys.answers.api.AnswerDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitSurveyDto {
    private List<AnswerDto> answers;
}