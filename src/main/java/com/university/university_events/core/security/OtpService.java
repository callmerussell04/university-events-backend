package com.university.university_events.core.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.university.university_events.users.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration; // Импортируем Duration
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit; // Для Caffeine

import jakarta.annotation.PostConstruct; // Для инициализации кэша

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_DURATION_MINUTES = 5;
    private static final long RESET_TOKEN_VALID_DURATION_MINUTES = 15;

    @Autowired
    private EmailService emailService;

    // Кэш для хранения OTP: ключ - username, значение - OTP
    private Cache<String, String> otpCache;

    private Cache<String, String> passwordResetOtpCache;

    private Cache<String, String> resetPasswordTokenCache;

    @PostConstruct // Инициализируем кэш после создания бина
    public void init() {
        otpCache = Caffeine.newBuilder()
                .expireAfterWrite(OTP_VALID_DURATION_MINUTES, TimeUnit.MINUTES) // OTP истекает через 5 минут
                .maximumSize(10_000) // Максимальное количество элементов в кэше
                .build();
        
        passwordResetOtpCache = Caffeine.newBuilder()
                .expireAfterWrite(OTP_VALID_DURATION_MINUTES, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
        
        resetPasswordTokenCache = Caffeine.newBuilder() // Инициализируем новый кэш
                .expireAfterWrite(RESET_TOKEN_VALID_DURATION_MINUTES, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    public String generateAndSendOtp(UserEntity user) {
        String otp = generateOtp();
        otpCache.put(user.getUsername(), otp); // Сохраняем OTP в кэше с ключом - username

        emailService.sendOtpEmail(user.getEmail(), otp); // Отправляем OTP на почту
        return otp;
    }

    public boolean validateOtp(UserEntity user, String providedOtp) {
        String storedOtp = otpCache.getIfPresent(user.getUsername()); // Получаем OTP из кэша

        if (storedOtp == null) {
            return false; // OTP не найден или истек
        }

        return storedOtp.equals(providedOtp);
    }

    public void clearOtp(UserEntity user) {
        otpCache.invalidate(user.getUsername()); // Удаляем OTP из кэша после использования
    }

    public String generateAndSendPasswordResetOtp(String email) {
        String otp = generateOtp();
        passwordResetOtpCache.put(email, otp); // Ключом будет email
        emailService.sendPasswordResetOtpEmail(email, otp);
        return otp;
    }

    public boolean validatePasswordResetOtp(String email, String providedOtp) {
        String storedOtp = passwordResetOtpCache.getIfPresent(email);
        if (storedOtp == null) {
            return false;
        }
        return storedOtp.equals(providedOtp);
    }

    public void clearPasswordResetOtp(String email) {
        passwordResetOtpCache.invalidate(email);
    }

    public String generateResetToken(String email) {
        String token = UUID.randomUUID().toString();
        resetPasswordTokenCache.put(token, email); // Ключ: токен, значение: email
        return token;
    }

    public String validateResetToken(String token) {
        // Возвращает email, если токен валиден и не истек, иначе null
        return resetPasswordTokenCache.getIfPresent(token);
    }

    public void clearResetToken(String token) {
        resetPasswordTokenCache.invalidate(token);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000); // Генерируем 6-значный OTP
        return String.valueOf(otp);
    }
}