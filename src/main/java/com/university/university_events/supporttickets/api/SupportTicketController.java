package com.university.university_events.supporttickets.api;

import org.modelmapper.ModelMapper;
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
import com.university.university_events.supporttickets.model.SupportTicketEntity;
import com.university.university_events.supporttickets.service.SupportTicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/support-ticket")
public class SupportTicketController {
    private final SupportTicketService supportticketService;
    private final ModelMapper modelMapper;

    public SupportTicketController(SupportTicketService supportticketService, ModelMapper modelMapper) {
        this.supportticketService = supportticketService;
        this.modelMapper = modelMapper;
    }

    private SupportTicketDto toDto(SupportTicketEntity entity) {
        return modelMapper.map(entity, SupportTicketDto.class);
    }

    private SupportTicketEntity toEntity(SupportTicketDto dto) {
        return modelMapper.map(dto, SupportTicketEntity.class);
    }

    @GetMapping
    public PageDto<SupportTicketDto> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return PageDtoMapper.toDto(supportticketService.getAll(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
    }

    @GetMapping("/unanswered")
    public PageDto<SupportTicketDto> getAllUnanswered(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return PageDtoMapper.toDto(supportticketService.getUnansweredTickets(page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
    }

    @GetMapping("/{id}")
    public SupportTicketDto get(@PathVariable(name = "id") Long id) {
        return toDto(supportticketService.get(id));
    }

    @PostMapping
    public SupportTicketDto create(@RequestBody @Valid SupportTicketDto dto) {
        return toDto(supportticketService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public SupportTicketDto update(@PathVariable(name = "id") Long id, @RequestBody SupportTicketDto dto) {
        return toDto(supportticketService.update(id, toEntity(dto)));
    }
}
