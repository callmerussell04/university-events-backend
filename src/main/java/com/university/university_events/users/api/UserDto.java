package com.university.university_events.users.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 30)
    private String name;
    private String login;
    private String phoneNumber;
    private String password;
    private String role;

    @Min(1)
    private Long groupId;
}