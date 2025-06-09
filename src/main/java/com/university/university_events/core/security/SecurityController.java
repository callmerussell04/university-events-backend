package com.university.university_events.core.security;

import com.university.university_events.core.configuration.Constants;
import com.university.university_events.core.security.OtpService;
import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.model.UserRole;
import com.university.university_events.users.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Constants.API_URL + "/auth")
public class SecurityController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpService otpService;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequestDto signinRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password.");
        }


        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Should not happen after authentication

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // --- Логика 2FA для студентов ---
        if (userEntity.getRole() == UserRole.STUDENT) {
            // Если OTP предоставлен в запросе, значит, это второй шаг аутентификации
            if (signinRequest.getOtp() != null && !signinRequest.getOtp().isEmpty()) {
                // Валидируем OTP, используя OtpService (который теперь работает с кэшем)
                if (otpService.validateOtp(userEntity, signinRequest.getOtp())) {
                    otpService.clearOtp(userEntity); // Удаляем OTP из кэша после успешной проверки
                    SecurityContextHolder.getContext().setAuthentication(authentication); // Устанавливаем аутентификацию в SecurityContext
                    String jwt = jwtUtils.generateJwtToken(authentication);
                    return ResponseEntity.ok(new JwtResponse(jwt,
                            userPrincipal.getId(),
                            userPrincipal.getUsername(),
                            roles)); // 2FA пройдена, токен выдан
                } else {
                    return ResponseEntity.badRequest().body("Invalid or expired OTP.");
                }
            } else {
                // Первый шаг аутентификации для студента: запрашиваем OTP
                otpService.generateAndSendOtp(userEntity); // Генерируем и отправляем OTP
                // Возвращаем ответ, указывающий на необходимость 2FA, без JWT токена
                return ResponseEntity.ok(new JwtResponse(
                        userPrincipal.getId(),
                        userPrincipal.getUsername(),
                        roles,
                        true)); // mfaRequired = true
            }
        } else {
            // Для других ролей (ADMIN, EMPLOYEE) 2FA не требуется
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    roles)); // mfaRequired = false
        }
    }

    // Новый эндпоинт для повторной отправки OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody OtpResendRequestDto otpResendRequest) {
        UserEntity userEntity = userRepository.findByUsername(otpResendRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userEntity.getRole() != UserRole.STUDENT) {
            return ResponseEntity.badRequest().body("OTP is only required for students.");
        }

        otpService.generateAndSendOtp(userEntity);
        return ResponseEntity.ok("New OTP sent to your email.");
    }
}
