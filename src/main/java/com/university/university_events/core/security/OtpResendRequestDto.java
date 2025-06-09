package com.university.university_events.core.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpResendRequestDto {
    @NotBlank
    private String username;
}