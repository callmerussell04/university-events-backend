package com.university.university_events;

import java.util.Objects;

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

	public UniversityEventsApplication(FacultyService facultyService, GroupService groupService, UserService userService,
	LocationService locationService, EventService eventService) {
		this.facultyService = facultyService;
		this.groupService = groupService;
		this.userService = userService;
		this.locationService = locationService;
		this.eventService = eventService;
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
			final var group1 = groupService.create(new GroupEntity("ПИбд-31", faculty1));
			final var group2 = groupService.create(new GroupEntity("ПИбд-32", faculty1));
			final var group3 = groupService.create(new GroupEntity("ПИбд-33", faculty1));

            log.info("Create default user values");
			userService.create(new UserEntity("Иванов Иван Иванович", "ivanov.i", "+777777777", "1234", UserRole.STUDENT, group1));
			userService.create(new UserEntity("Сергеев Сергей Сергеевич", "sergeev.s", "+777777778", "1234", UserRole.STUDENT, group2));
			userService.create(new UserEntity("лох какой-то", "loh.k", "+777777779", "1234", UserRole.STUDENT, group3));
			userService.create(new UserEntity("нащальник", "boss", "+7777777777", "1234", UserRole.EMPLOYEE, null));
		
            log.info("Create default location values");
            final var location1 = locationService.create(new LocationEntity("Тарелка"));
            final var location2 = locationService.create(new LocationEntity("Аудитория 1"));
            final var location3 = locationService.create(new LocationEntity("Аудитория 2"));
            final var location4 = locationService.create(new LocationEntity("Аудитория 3"));
			
            log.info("Create default location values");
			eventService.create(new EventEntity("мероприятие 1", EventStatus.PLANNED, Formatter.parse("2025-06-11"), "организатор", location1, null));
			eventService.create(new EventEntity("мероприятие 2", EventStatus.PLANNED, Formatter.parse("2025-06-11"), "организатор", location2, null));
			eventService.create(new EventEntity("мероприятие 3", EventStatus.PLANNED, Formatter.parse("2025-06-12"), "организатор", location1, null));
			eventService.create(new EventEntity("мероприятие 4", EventStatus.PLANNED, Formatter.parse("2025-06-11"), "организатор", location3, null));
			eventService.create(new EventEntity("мероприятие 5", EventStatus.PLANNED, Formatter.parse("2025-06-13"), "организатор", location4, null));
			eventService.create(new EventEntity("мероприятие 6", EventStatus.ACTIVE, Formatter.parse("2025-06-13"), "организатор", location4, null));
			eventService.create(new EventEntity("мероприятие 7", EventStatus.ACTIVE, Formatter.parse("2025-06-13"), "организатор", location3, null));
		}
	}
}
