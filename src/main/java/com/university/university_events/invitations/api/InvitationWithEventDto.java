package com.university.university_events.invitations.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.university.university_events.events.api.EventDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationWithEventDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Min(1)
    private Long userId;
    
    @NotBlank
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EventDto event;
}