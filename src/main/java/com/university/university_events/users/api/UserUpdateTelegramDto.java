// com/university/university_events/users/api/UserUpdateTelegramDto.java (NEW DTO)
package com.university.university_events.users.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateTelegramDto {
    @NotNull
    private Long userId;
    @NotBlank
    private String telegramChatId;
}