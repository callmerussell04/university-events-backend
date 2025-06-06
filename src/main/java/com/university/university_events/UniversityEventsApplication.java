package com.university.university_events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.model.EventStatus;
import com.university.university_events.events.service.EventService;
import com.university.university_events.faculties.model.FacultyEntity;
import com.university.university_events.faculties.service.FacultyService;
import com.university.university_events.groups.model.GroupEntity;
import com.university.university_events.groups.service.GroupService;
import com.university.university_events.locations.model.LocationEntity;
import com.university.university_events.locations.service.LocationService;
import com.university.university_events.surveys.answers.api.AnswerDto;
import com.university.university_events.surveys.model.SurveyEntity;
import com.university.university_events.surveys.options.model.OptionEntity;
import com.university.university_events.surveys.questions.model.QuestionEntity;
import com.university.university_events.surveys.service.SurveyService;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.model.UserRole;
import com.university.university_events.users.service.UserService;
import com.university.university_events.core.utils.Formatter;

@SpringBootApplication
public class UniversityEventsApplication implements CommandLineRunner {
	private final Logger log = LoggerFactory.getLogger(UniversityEventsApplication.class);

	private final FacultyService facultyService;
	private final GroupService groupService;
	private final UserService userService;
	private final LocationService locationService;
	private final EventService eventService;
	private final SurveyService surveyService;

	public UniversityEventsApplication(FacultyService facultyService, GroupService groupService, UserService userService,
	LocationService locationService, EventService eventService, SurveyService surveyService) {
		this.facultyService = facultyService;
		this.groupService = groupService;
		this.userService = userService;
		this.locationService = locationService;
		this.eventService = eventService;
		this.surveyService = surveyService;
	}

	public static void main(String[] args) {
		SpringApplication.run(UniversityEventsApplication.class, args);
	}

	@Override
    public void run(String... args) throws Exception {
		if (args.length > 0 && Objects.equals("--populate", args[0])) {
            log.info("Create default faculty values");
            final var faculty1 = facultyService.create(new FacultyEntity("ФИСТ"));

            log.info("Create default group values");
			final var group1 = groupService.create(new GroupEntity("ПИбд-31", 3, faculty1));
			final var group2 = groupService.create(new GroupEntity("ПИбд-32",3,  faculty1));
			final var group3 = groupService.create(new GroupEntity("ПИбд-33", 3, faculty1));

            log.info("Create default user values");
			final var user1 = userService.create(new UserEntity("Иванов Иван Иванович", "ivanov@email.com", "ivanov.i", "+777777777", "Qwer1234!", UserRole.STUDENT, group1));
			userService.create(new UserEntity("Сергеев Сергей Сергеевич","sergeev@email.com", "sergeev.s", "+777777778", "Qwer1234!", UserRole.STUDENT, group2));
			userService.create(new UserEntity("лох какой-то","loh@email.com", "loh.k", "+777777779", "Qwer1234!", UserRole.STUDENT, group3));
			userService.create(new UserEntity("нащальник","boss@email.com", "boss", "+7777777777", "Qwer1234!", UserRole.EMPLOYEE, null));
		
            log.info("Create default location values");
            final var location1 = locationService.create(new LocationEntity("Тарелка"));
            final var location2 = locationService.create(new LocationEntity("Аудитория 1"));
            final var location3 = locationService.create(new LocationEntity("Аудитория 2"));
            final var location4 = locationService.create(new LocationEntity("Аудитория 3"));
			
            log.info("Create default event values");
			eventService.create(new EventEntity("мероприятие 1", EventStatus.PLANNED, Formatter.parse("2025-06-11 10:00"), Formatter.parse("2025-06-11 10:40"), "организатор", location1, null));
			eventService.create(new EventEntity("мероприятие 2", EventStatus.PLANNED, Formatter.parse("2025-06-11 10:00"), Formatter.parse("2025-06-11 10:40"), "организатор", location2, null));
			eventService.create(new EventEntity("мероприятие 3", EventStatus.PLANNED, Formatter.parse("2025-06-12 10:00"), Formatter.parse("2025-06-12 11:30"), "организатор", location1, null));
			eventService.create(new EventEntity("мероприятие 4", EventStatus.PLANNED, Formatter.parse("2025-06-11 10:00"), Formatter.parse("2025-06-11 10:40"), "организатор", location3, null));
			eventService.create(new EventEntity("мероприятие 5", EventStatus.PLANNED, Formatter.parse("2025-06-13 10:00"), Formatter.parse("2025-06-13 10:40"), "организатор", location4, null));
			eventService.create(new EventEntity("мероприятие 6", EventStatus.ACTIVE, Formatter.parse("2025-06-13 10:00"), Formatter.parse("2025-06-13 10:30"), "организатор", location4, null));
			eventService.create(new EventEntity("мероприятие 7", EventStatus.ACTIVE, Formatter.parse("2025-06-13 10:00"), Formatter.parse("2025-06-13 10:50"), "организатор", location3, null));

			SurveyEntity surveyEntity = new SurveyEntity();
			surveyEntity.setName("survey");

			QuestionEntity question = new QuestionEntity();
			question.setText("?");

			OptionEntity option1 = new OptionEntity();
			option1.setText("да");
			OptionEntity option2 = new OptionEntity();
			option2.setText("нет");
			List<OptionEntity> options = new ArrayList<OptionEntity>(Arrays.asList(option1, option2));
			question.setOptions(options);

			List<QuestionEntity> quetions = new ArrayList<QuestionEntity>(Arrays.asList(question));
			surveyEntity.setQuestions(quetions);

			final var survey1 = surveyService.create(surveyEntity);

			AnswerDto answer1 = new AnswerDto();
			answer1.setQuestionId(survey1.getQuestions().getFirst().getId());
			answer1.setUserId(user1.getId());
			answer1.setOptionId(survey1.getQuestions().getFirst().getOptions().getFirst().getId());

			AnswerDto answer2 = new AnswerDto();
			answer2.setQuestionId(survey1.getQuestions().getFirst().getId());
			answer2.setUserId(user1.getId());
			answer2.setOptionId(survey1.getQuestions().getFirst().getOptions().get(1).getId());

			List<AnswerDto> answerDtos = new ArrayList<AnswerDto>();
			answerDtos.add(answer1);
			answerDtos.add(answer2);

			surveyService.submitSurvey(user1.getId(), answerDtos);

			IntStream.range(11, 100)
                    .forEach(value -> groupService.create(
                            new GroupEntity("group-".concat(String.valueOf(value)), 1, faculty1)));	}
	}
}
