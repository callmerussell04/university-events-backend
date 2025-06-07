package com.university.university_events.surveys.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.university_events.core.error.NotFoundException;
import com.university.university_events.core.service.AbstractService;
import com.university.university_events.surveys.answers.api.AnswerDto;
import com.university.university_events.surveys.answers.model.AnswerEntity;
import com.university.university_events.surveys.answers.repository.AnswerRepository;
import com.university.university_events.surveys.api.SubmitSurveyDto;
import com.university.university_events.surveys.model.SurveyEntity;
import com.university.university_events.surveys.options.model.OptionEntity;
import com.university.university_events.surveys.options.repository.OptionRepository;
import com.university.university_events.surveys.questions.model.QuestionEntity;
import com.university.university_events.surveys.questions.repository.QuestionRepository;
import com.university.university_events.surveys.repository.SurveyRepository;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.service.UserService;

@Service
public class SurveyService extends AbstractService<SurveyEntity> {
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;


    public SurveyService(SurveyRepository surveyRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, UserService userService, OptionRepository optionRepository) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userService = userService;
        this.optionRepository = optionRepository;
    }

    @Transactional(readOnly = true)
    public List<SurveyEntity> getAll() {
        return StreamSupport.stream(surveyRepository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public SurveyEntity get(long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SurveyEntity.class, id));
    }

    @Transactional
    public SurveyEntity create(SurveyEntity surveyEntity) {
        validate(surveyEntity, true);
        List<QuestionEntity> questionEntities = surveyEntity.getQuestions();
		for (QuestionEntity questionEntity : questionEntities) {
			questionEntity.setSurvey(surveyEntity);
            List<OptionEntity> optionEntities = questionEntity.getOptions();
            if (optionEntities != null)
                for (OptionEntity optionEntity : optionEntities)
                    optionEntity.setQuestion(questionEntity);
		}
        return surveyRepository.save(surveyEntity);
    }

    // @Transactional
    // public SurveyEntity update(Long id, SurveyEntity entity) {
    //     validate(entity, false);
    //     final SurveyEntity existsEntity = get(id);
    //     existsEntity.setName(entity.getName());
    //     return surveyRepository.save(existsEntity);
    // }

    @Transactional
    public SurveyEntity delete(Long id) {
        final SurveyEntity existsEntity = get(id);
        surveyRepository.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<QuestionEntity> getQuestionsByIds(Collection<Long> ids) {
        final List<QuestionEntity> questions = StreamSupport.stream(questionRepository.findAllById(ids).spliterator(), false).toList();
        if (questions.size() < ids.size()) {
            throw new IllegalArgumentException("Invalid item");
        }
        return questions;
    }

    @Transactional(readOnly = true)
    public List<OptionEntity> getOptionsByIds(Collection<Long> ids) {
        final List<OptionEntity> options = StreamSupport.stream(optionRepository.findAllById(ids).spliterator(), false).toList();
        if (options.size() < ids.size()) {
            throw new IllegalArgumentException("Invalid item");
        }
        return options;
    }

    @Transactional
    public void submitSurvey(SubmitSurveyDto submitSurveyDto) {
        List<AnswerDto> answerDtoList = submitSurveyDto.getAnswers();
        final Set<Long> questionIds = answerDtoList.stream()
                .map(AnswerDto::getQuestionId)
                .collect(Collectors.toSet());
        final Map<Long, QuestionEntity> questions = getQuestionsByIds(questionIds).stream()
                .collect(Collectors.toMap(QuestionEntity::getId, Function.identity()));
        
        final Set<Long> optionIds = answerDtoList.stream()
                .map(AnswerDto::getOptionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        final Map<Long, OptionEntity> options = getOptionsByIds(optionIds).stream()
                .collect(Collectors.toMap(OptionEntity::getId, Function.identity()));

        UserEntity user = userService.get(submitSurveyDto.getUserId());

        List<AnswerEntity> answerEntitiesList = new ArrayList<AnswerEntity>();

        for (AnswerDto element : answerDtoList) {
            AnswerEntity answerEntity = new AnswerEntity(questions.get(element.getQuestionId()), user, options.get(element.getOptionId()), element.getText());
            answerEntitiesList.add(answerEntity);
        }

        answerRepository.saveAll(answerEntitiesList);
    }

    @Override
    protected void validate(SurveyEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Survey entity is null");
        }
        validateStringField(entity.getName(), "Survey name");
        if (uniqueCheck) {
            if (surveyRepository.findByNameIgnoreCase(entity.getName()).isPresent()) {
                throw new IllegalArgumentException(
                        String.format("Survey with name %s already exists", entity.getName())
                );
            }
        }
    }
}
