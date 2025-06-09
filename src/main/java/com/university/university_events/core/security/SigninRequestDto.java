package com.university.university_events.core.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SigninRequestDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String otp;
}