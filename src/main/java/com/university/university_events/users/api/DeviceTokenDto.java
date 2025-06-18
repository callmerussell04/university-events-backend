// packages/com/university/university_events/users/api/DeviceTokenDto.java (создайте этот файл)
package com.university.university_events.users.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceTokenDto {
    @NotBlank(message = "Device token cannot be empty")
    private String deviceToken;
}