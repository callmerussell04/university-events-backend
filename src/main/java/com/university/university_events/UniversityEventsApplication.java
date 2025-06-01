package com.university.university_events;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.university.university_events.faculties.model.FacultyEntity;
import com.university.university_events.faculties.service.FacultyService;
import com.university.university_events.groups.model.GroupEntity;
import com.university.university_events.groups.service.GroupService;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.model.UserRole;
import com.university.university_events.users.service.UserService;

@SpringBootApplication
public class UniversityEventsApplication implements CommandLineRunner {
	private final Logger log = LoggerFactory.getLogger(UniversityEventsApplication.class);

	private final FacultyService facultyService;
	private final GroupService groupService;
	private final UserService userService;

	public UniversityEventsApplication(FacultyService facultyService, GroupService groupService, UserService userService) {
		this.facultyService = facultyService;
		this.groupService = groupService;
		this.userService = userService;
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
			userService.create(new UserEntity("eblan", "eblan228", "+777777777", "1234", UserRole.STUDENT, group1));
			userService.create(new UserEntity("eblan eblanov", "eblan1337", "+777777778", "1234", UserRole.STUDENT, group2));
			userService.create(new UserEntity("eblan eblanov eblanovich", "eblan420", "+777777779", "1234", UserRole.STUDENT, group3));
			userService.create(new UserEntity("нащальник", "boss", "+7777777777", "1234", UserRole.EMPLOYEE, null));
		}
	}
}
