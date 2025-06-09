package com.university.university_events.invitations.api;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.university.university_events.core.api.PageDto;
import com.university.university_events.core.api.PageDtoMapper;
import com.university.university_events.core.configuration.Constants;
import com.university.university_events.core.utils.Formatter;
import com.university.university_events.events.api.EventDto;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.service.EventService;
import com.university.university_events.groups.model.GroupEntity;
import com.university.university_events.groups.service.GroupService;
import com.university.university_events.invitations.model.InvitationStatus;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.invitations.service.InvitationService;
import com.university.university_events.users.model.UserEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    private final GroupService groupService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    BiMap<String, String> statusMap = HashBiMap.create();
    BiMap<String, String> eventStatusMap = HashBiMap.create();

    public InvitationController(InvitationService invitationService, ModelMapper modelMapper, GroupService groupService, EventService eventService) {
        this.invitationService = invitationService;
        this.modelMapper = modelMapper;
        statusMap.put("ATTENDED", "Посетил");
        statusMap.put("NOT_ATTENDED", "Не посетил");
        this.groupService = groupService;
        this.eventService = eventService;
        eventStatusMap.put("PLANNED", "Запланировано");
        eventStatusMap.put("ACTIVE", "В процессе");
        eventStatusMap.put("COMPLETED", "Завершено");
        eventStatusMap.put("CANCELED", "Отменено");
    }

    private EventDto toEventDto(EventEntity entity) {
        final EventDto dto = modelMapper.map(entity, EventDto.class);
        dto.setStatus(eventStatusMap.get(dto.getStatus()));
        dto.setStartDateTime(Formatter.formatWithTime(entity.getStartDateTime()));
        dto.setEndDateTime(Formatter.formatWithTime(entity.getEndDateTime()));
        return dto;
    }

    private InvitationWithEventDto toInvitationWithEventDto(InvitationEntity entity) {
        final InvitationWithEventDto dto = modelMapper.map(entity, InvitationWithEventDto.class);
        dto.setStatus(statusMap.get(dto.getStatus()));
        dto.setEvent(toEventDto(entity.getEvent()));
        return dto;
    }

    private InvitationDto toDto(InvitationEntity entity) {
        final InvitationDto dto = modelMapper.map(entity, InvitationDto.class);
        dto.setStatus(statusMap.get(dto.getStatus()));
        return dto;
    }

    private InvitationEntity toEntity(InvitationDto dto) {
        dto.setStatus(statusMap.inverse().get(dto.getStatus()));
        return modelMapper.map(dto, InvitationEntity.class);
    }

    @GetMapping
    public PageDto<InvitationDto> getAll(
            @RequestParam(name = "eventId", defaultValue = "0") Long eventId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return PageDtoMapper.toDto(invitationService.getAll(eventId, page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
    }

    @GetMapping("/{id}")
    public InvitationDto get(@PathVariable(name = "id") Long id) {
        return toDto(invitationService.get(id));
    }

    @PostMapping
    public InvitationDto create(@RequestBody @Valid InvitationDto dto) {
        return toDto(invitationService.create(toEntity(dto)));
    }

    @PostMapping("/invite-group")
    public ResponseEntity<String> inviteGroup(@RequestBody @Valid GroupInvitationDto dto) {
        EventEntity eventEntity = eventService.get(dto.getEventId());
        List<UserEntity> students = groupService.get(dto.getGroupId()).getUsers();
        List<InvitationEntity> invitations = new ArrayList<>();
        for (UserEntity userEntity : students) {
            invitations.add(new InvitationEntity(userEntity, eventEntity, InvitationStatus.NOT_ATTENDED));
        }
        invitationService.create(invitations);
        return ResponseEntity.ok("Сохранено " + invitations.size() + " записей");
    }

    @PostMapping("/invite-course")
    public ResponseEntity<String> inviteCourse(@RequestBody @Valid CourseInvitationDto dto) {
        EventEntity eventEntity = eventService.get(dto.getEventId());
        List<GroupEntity> groups = groupService.findByFacultyAndCourse(dto.getFacultyId(), dto.getCourse());
        List<UserEntity> students = new ArrayList<>();
        for (GroupEntity groupEntity : groups) {
            students.addAll(groupEntity.getUsers());
        }
        List<InvitationEntity> invitations = new ArrayList<>();
        for (UserEntity userEntity : students) {
            invitations.add(new InvitationEntity(userEntity, eventEntity, InvitationStatus.NOT_ATTENDED));
        }
        invitationService.create(invitations);
        return ResponseEntity.ok("Сохранено " + invitations.size() + " записей");
    }

    @PutMapping("/{id}")
    public InvitationDto update(@PathVariable(name = "id") Long id, @RequestBody InvitationDto dto) {
        return toDto(invitationService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public InvitationDto delete(@PathVariable(name = "id") Long id) {
        return toDto(invitationService.delete(id));
    }

    @GetMapping("/by-user/{userId}")
    public PageDto<InvitationWithEventDto> getInvitationsByUserId(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return PageDtoMapper.toDto(invitationService.getInvitationsByUserId(userId, page, Constants.DEFUALT_PAGE_SIZE), this::toInvitationWithEventDto);
    }
}
