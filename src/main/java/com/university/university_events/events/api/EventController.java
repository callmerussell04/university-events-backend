package com.university.university_events.events.api;

import java.text.ParseException;
import java.util.Date;

import org.modelmapper.ModelMapper;
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
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/event")
public class EventController {
    private final EventService eventService;
    private final ModelMapper modelMapper;
    BiMap<String, String> statusMap = HashBiMap.create();

    public EventController(EventService eventService, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        statusMap.put("PLANNED", "Запланировано");
        statusMap.put("ACTIVE", "В процессе");
        statusMap.put("COMPLETED", "Завершено");
        statusMap.put("CANCELED", "Отменено");
    }

    private EventDto toDto(EventEntity entity) {
        final EventDto dto = modelMapper.map(entity, EventDto.class);
        dto.setStatus(statusMap.get(dto.getStatus()));
        dto.setStartDateTime(Formatter.format(entity.getStartDateTime()));
        dto.setEndDateTime(Formatter.format(entity.getEndDateTime()));
        return dto;
    }

    private EventEntity toEntity(EventDto dto) throws ParseException {
        dto.setStatus(statusMap.inverse().get(dto.getStatus()));
        final EventEntity entity = modelMapper.map(dto, EventEntity.class);
        entity.setStartDateTime(Formatter.parse(dto.getStartDateTime()));
        entity.setEndDateTime(Formatter.parse(dto.getEndDateTime()));
        return entity;
    }
    
    @GetMapping
    public PageDto<EventDto> getAll (
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "locationId", required = false) Long locationId,
            @RequestParam(name = "startDate", required = false) String startDateString,
            @RequestParam(name = "endDate", required = false) String endDateString,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "0") int page) throws ParseException {
        Date startDate = startDateString != null ? Formatter.parse(startDateString) : null;
        Date endDate = endDateString != null ? Formatter.parse(endDateString) : null;
        return PageDtoMapper.toDto(eventService.getAll(status, locationId, startDate, endDate, name, page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
    }

    @GetMapping("/{id}")
    public EventDto get(@PathVariable(name = "id") Long id) {
        return toDto(eventService.get(id));
    }

    @PostMapping
    public EventDto create(@RequestBody @Valid EventDto dto) throws ParseException {
        return toDto(eventService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable(name = "id") Long id, @RequestBody EventDto dto) throws ParseException {
        return toDto(eventService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public EventDto delete(@PathVariable(name = "id") Long id) {
        return toDto(eventService.delete(id));
    }
}
