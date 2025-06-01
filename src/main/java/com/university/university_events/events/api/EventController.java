package com.university.university_events.events.api;

import java.text.ParseException;

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

    public EventController(EventService eventService, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    private EventDto toDto(EventEntity entity) {
        final EventDto dto = modelMapper.map(entity, EventDto.class);
        dto.setDate(Formatter.format(entity.getDateTime()));
        return dto;
    }

    private EventEntity toEntity(EventDto dto) throws ParseException {
        final EventEntity entity = modelMapper.map(dto, EventEntity.class);
        entity.setDateTime(Formatter.parse(dto.getDate()));
        return entity;
    }
    
    @GetMapping
    public PageDto<EventDto> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return PageDtoMapper.toDto(eventService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
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
