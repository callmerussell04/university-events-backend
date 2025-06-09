package com.university.university_events.core.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
    @NotBlank(message = "Reset token cannot be empty")
    private String resetToken;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 8, max = 60, message = "Password must be between 8 and 60 characters")
    private String newPassword;
}
