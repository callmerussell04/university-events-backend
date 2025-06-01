package com.university.university_events.faculties.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.university.university_events.groups.api.GroupDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 30)
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<GroupDto> groups;
}