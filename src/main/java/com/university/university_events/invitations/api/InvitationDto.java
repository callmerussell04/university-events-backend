package com.university.university_events.invitations.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Min(1)
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userName;

    @NotNull
    @Min(1)
    private Long eventId;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String eventName;

    @NotBlank
    private String status;
}