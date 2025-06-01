package com.university.university_events.faculties.api;

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
import com.university.university_events.faculties.model.FacultyEntity;
import com.university.university_events.faculties.service.FacultyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/faculty")
public class FacultyController {
    private final FacultyService facultyService;
    private final ModelMapper modelMapper;

    public FacultyController(FacultyService facultyService, ModelMapper modelMapper) {
        this.facultyService = facultyService;
        this.modelMapper = modelMapper;
    }

    private FacultyDto toDto(FacultyEntity entity) {
        return modelMapper.map(entity, FacultyDto.class);
    }

    private FacultyEntity toEntity(FacultyDto dto) {
        return modelMapper.map(dto, FacultyEntity.class);
    }

    @GetMapping
    public List<FacultyDto> getAll() {
        return facultyService.getAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public FacultyDto get(@PathVariable(name = "id") Long id) {
        return toDto(facultyService.get(id));
    }

    @PostMapping
    public FacultyDto create(@RequestBody @Valid FacultyDto dto) {
        return toDto(facultyService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public FacultyDto update(@PathVariable(name = "id") Long id, @RequestBody FacultyDto dto) {
        return toDto(facultyService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public FacultyDto delete(@PathVariable(name = "id") Long id) {
        return toDto(facultyService.delete(id));
    }
}
