package com.university.university_events.users.api;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.university_events.core.configuration.Constants;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.university.university_events.core.api.PageDto;
import com.university.university_events.core.api.PageDtoMapper;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(Constants.API_URL + "/user")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    BiMap<String, String> roleMap = HashBiMap.create();
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        roleMap.put("ADMIN", "Администратор");
        roleMap.put("EMPLOYEE", "Сотрудник");
        roleMap.put("STUDENT", "Студент");
        this.passwordEncoder = passwordEncoder;
    }

    private UserDto toDto(UserEntity entity) {
        final UserDto dto = modelMapper.map(entity, UserDto.class);
        dto.setRole(roleMap.get(dto.getRole()));
        return dto;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder(length);
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private UserEntity toEntity(UserDto dto) {
        dto.setRole(roleMap.inverse().get(dto.getRole()));
        if (dto.getPassword() == null || dto.getPassword().isEmpty())
            dto.setPassword(generateRandomPassword(10));
        dto.setPassword(
                passwordEncoder.encode(dto.getPassword().strip()));
        return modelMapper.map(dto, UserEntity.class);
    }

    @GetMapping
    public PageDto<UserDto> getAll(
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return PageDtoMapper.toDto(userService.getAll(roleMap.inverse().get(role), page, Constants.DEFUALT_PAGE_SIZE), this::toDto);
    }

    @GetMapping("/no-pages")
    public List<UserDto> getAll(
            @RequestParam(name = "role", required = false) String role) {
        return userService.getAll(roleMap.inverse().get(role)).stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable(name = "id") Long id) {
        return toDto(userService.get(id));
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto dto) {
        return toDto(userService.create(toEntity(dto)));
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable(name = "id") Long id, @RequestBody UserDto dto) {
        return toDto(userService.update(id, toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable(name = "id") Long id) {
        return toDto(userService.delete(id));
    }

    @PostMapping("/{id}/device-token")
    public ResponseEntity<Void> updateDeviceToken(@PathVariable(name = "id") Long id, @RequestBody @Valid DeviceTokenDto dto) {
        userService.updateDeviceToken(id, dto.getDeviceToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/link-telegram")
    public ResponseEntity<String> linkTelegramChatId(
            @RequestBody @Valid UserUpdateTelegramDto dto) {
        try {
            UserEntity user = userService.get(dto.getUserId());
            user.setTelegramChatId(dto.getTelegramChatId());
            userService.update(dto.getUserId(), user);

            return ResponseEntity.ok("Telegram Chat ID linked successfully for user: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error linking Telegram Chat ID: " + e.getMessage());
        }
    }
}
