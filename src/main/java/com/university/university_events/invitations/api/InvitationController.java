package com.university.university_events.invitations.api;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.university_events.core.configuration.Constants;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.invitations.service.InvitationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/invitation")
public class InvitationController {
    private final InvitationService invitationService;
    private final ModelMapper modelMapper;

    public InvitationController(InvitationService invitationService, ModelMapper modelMapper) {
        this.invitationService = invitationService;
        this.modelMapper = modelMapper;
    }

    private InvitationDto toDto(InvitationEntity entity) {
        return modelMapper.map(entity, InvitationDto.class);
    }

    private InvitationEntity toEntity(InvitationDto dto) {
        return modelMapper.map(dto, InvitationEntity.class);
    }

    @GetMapping
    public List<InvitationDto> getAll() {
        return invitationService.getAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public InvitationDto get(@PathVariable(name = "id") Long id) {
        return toDto(invitationService.get(id));
    }

    @PostMapping
    public InvitationDto create(@RequestBody @Valid InvitationDto dto) {
        return toDto(invitationService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public InvitationDto update(@PathVariable(name = "id") Long id, @RequestBody InvitationDto dto) {
        return toDto(invitationService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public InvitationDto delete(@PathVariable(name = "id") Long id) {
        return toDto(invitationService.delete(id));
    }
}
