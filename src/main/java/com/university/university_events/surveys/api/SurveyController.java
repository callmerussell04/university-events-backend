package com.university.university_events.surveys.api;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.university_events.core.configuration.Constants;
import com.university.university_events.surveys.model.SurveyEntity;
import com.university.university_events.surveys.service.SurveyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/survey")
public class SurveyController {
    private final SurveyService surveyService;
    private final ModelMapper modelMapper;

    public SurveyController(SurveyService surveyService, ModelMapper modelMapper) {
        this.surveyService = surveyService;
        this.modelMapper = modelMapper;
    }

    private SurveyDto toDto(SurveyEntity entity) {
        return modelMapper.map(entity, SurveyDto.class);
    }

    private SurveyEntity toEntity(SurveyDto dto) {
        return modelMapper.map(dto, SurveyEntity.class);
    }

    @GetMapping
    public List<SurveyDto> getAll() {
        return surveyService.getAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public SurveyDto get(@PathVariable(name = "id") Long id) {
        return toDto(surveyService.get(id));
    }

    @PostMapping
    public SurveyDto create(@RequestBody @Valid SurveyDto dto) {
        return toDto(surveyService.create(toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public SurveyDto delete(@PathVariable(name = "id") Long id) {
        return toDto(surveyService.delete(id));
    }

    @PostMapping("/take-survey")
    public void submitSurvey(@RequestParam(name = "userId") Long userId, @RequestBody @Valid SubmitSurveyDto dto) {
        surveyService.submitSurvey(userId, dto.getAnswers());
    }
}
