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
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 1, max = 30)
    private String username;

    @NotBlank
    @Size(min = 1, max = 15)
    private String phoneNumber;

    private String password;

    @NotBlank
    private String role;

    @Min(1)
    private Long groupId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String groupName;

    private String deviceToken;
}