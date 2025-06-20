package com.university.university_events.events.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    private String status;

    @NotBlank
    private String startDateTime;

    @NotBlank
    private String endDateTime;

    @NotBlank
    @Size(min = 1, max = 30)
    private String organizer;

    @NotNull
    @Min(1)
    private Long locationId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String locationName;
}