package com.university.university_events.events.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.university.university_events.invitations.api.InvitationDto;

import jakarta.validation.constraints.NotBlank;
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
    private String date;

    @NotBlank
    @Size(min = 1, max = 30)
    private String location;

    @NotBlank
    @Size(min = 1, max = 30)
    private String organizer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<InvitationDto> invitaions;
}