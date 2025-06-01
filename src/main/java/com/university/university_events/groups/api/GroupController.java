package com.university.university_events.groups.api;

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
import com.university.university_events.groups.model.GroupEntity;
import com.university.university_events.groups.service.GroupService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/group")
public class GroupController {
    private final GroupService groupService;
    private final ModelMapper modelMapper;

    public GroupController(GroupService groupService, ModelMapper modelMapper) {
        this.groupService = groupService;
        this.modelMapper = modelMapper;
    }

    private GroupDto toDto(GroupEntity entity) {
        return modelMapper.map(entity, GroupDto.class);
    }

    private GroupEntity toEntity(GroupDto dto) {
        return modelMapper.map(dto, GroupEntity.class);
    }

    @GetMapping
    public List<GroupDto> getAll() {
        return groupService.getAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public GroupDto get(@PathVariable(name = "id") Long id) {
        return toDto(groupService.get(id));
    }

    @PostMapping
    public GroupDto create(@RequestBody @Valid GroupDto dto) {
        return toDto(groupService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public GroupDto update(@PathVariable(name = "id") Long id, @RequestBody GroupDto dto) {
        return toDto(groupService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public GroupDto delete(@PathVariable(name = "id") Long id) {
        return toDto(groupService.delete(id));
    }
}
