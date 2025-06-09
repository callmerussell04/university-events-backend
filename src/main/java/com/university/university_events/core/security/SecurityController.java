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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpService otpService;

    @Autowired
    EmailService emailService;

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

    // Шаг 1: Запрос сброса пароля (отправка OTP на почту)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElse(null);

        if (userEntity == null) {
            // Возвращаем общий успешный ответ, чтобы не давать подсказок злоумышленникам
            // о существовании email в системе. Реальное письмо не отправляется.
            return ResponseEntity.ok("Если аккаунт с таким email существует, OTP был отправлен.");
        }

        otpService.generateAndSendPasswordResetOtp(userEntity.getEmail());
        return ResponseEntity.ok("Если аккаунт с таким email существует, OTP был отправлен.");
    }

    // Шаг 2: Подтверждение OTP для сброса пароля и получение временного токена сброса
    @PostMapping("/verify-otp-for-reset")
    public ResponseEntity<?> verifyOtpForReset(@Valid @RequestBody VerifyResetOtpRequestDto verifyRequest) {
        UserEntity userEntity = userRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден для предоставленного email."));

        if (!otpService.validatePasswordResetOtp(verifyRequest.getEmail(), verifyRequest.getOtp())) {
            return ResponseEntity.badRequest().body("Неверный или истекший OTP для сброса пароля.");
        }

        // OTP успешно подтвержден, очищаем его из кэша
        otpService.clearPasswordResetOtp(verifyRequest.getEmail());

        // Генерируем и возвращаем временный токен сброса пароля
        String resetToken = otpService.generateResetToken(userEntity.getEmail());
        return ResponseEntity.ok(resetToken);
    }

    // Шаг 3: Установка нового пароля с использованием временного токена
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequest) {
        // Валидируем временный токен сброса
        String userEmail = otpService.validateResetToken(resetPasswordRequest.getResetToken());

        if (userEmail == null) {
            return ResponseEntity.badRequest().body("Недействительный или истекший токен сброса пароля.");
        }

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден для предоставленного email.")); // Маловероятно, но на всякий случай

        // Токен успешно подтвержден, очищаем его из кэша
        otpService.clearResetToken(resetPasswordRequest.getResetToken());

        // Хэшируем новый пароль и сохраняем его
        userEntity.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(userEntity);

        return ResponseEntity.ok("Пароль успешно сброшен.");
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<?> forgotUsername(@Valid @RequestBody ForgotUsernameRequestDto forgotUsernameRequest) {

        UserEntity userEntity = userRepository.findByEmail(forgotUsernameRequest.getEmail())
                .orElse(null); // Не бросаем исключение, чтобы не раскрывать информацию о существовании аккаунта

        if (userEntity == null) {
            // Возвращаем общий успешный ответ, чтобы не давать подсказок злоумышленникам
            // о существовании email в системе. Реальное письмо не отправляется.
            return ResponseEntity.ok("Если аккаунт с таким email существует, ваше имя пользователя было отправлено.");
        }

        // Отправляем имя пользователя асинхронно
        emailService.sendUsernameReminderEmail(userEntity.getEmail(), userEntity.getUsername());

        return ResponseEntity.ok("Если аккаунт с таким email существует, ваше имя пользователя было отправлено.");
    }
}
