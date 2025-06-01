package com.university.university_events.locations.api;

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
import com.university.university_events.locations.model.LocationEntity;
import com.university.university_events.locations.service.LocationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/location")
public class LocationController {
    private final LocationService locationService;
    private final ModelMapper modelMapper;

    public LocationController(LocationService locationService, ModelMapper modelMapper) {
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    private LocationDto toDto(LocationEntity entity) {
        return modelMapper.map(entity, LocationDto.class);
    }

    private LocationEntity toEntity(LocationDto dto) {
        return modelMapper.map(dto, LocationEntity.class);
    }

    @GetMapping
    public List<LocationDto> getAll() {
        return locationService.getAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public LocationDto get(@PathVariable(name = "id") Long id) {
        return toDto(locationService.get(id));
    }

    @PostMapping
    public LocationDto create(@RequestBody @Valid LocationDto dto) {
        return toDto(locationService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public LocationDto update(@PathVariable(name = "id") Long id, @RequestBody LocationDto dto) {
        return toDto(locationService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public LocationDto delete(@PathVariable(name = "id") Long id) {
        return toDto(locationService.delete(id));
    }
}
