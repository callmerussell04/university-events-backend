package com.university.university_events.core.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.university.university_events.users.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration; // Импортируем Duration
import java.util.Random;
import java.util.concurrent.TimeUnit; // Для Caffeine

import jakarta.annotation.PostConstruct; // Для инициализации кэша

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_DURATION_MINUTES = 5; // OTP действителен 5 минут

    @Autowired
    private EmailService emailService;

    // Кэш для хранения OTP: ключ - username, значение - OTP
    private Cache<String, String> otpCache;

    @PostConstruct // Инициализируем кэш после создания бина
    public void init() {
        otpCache = Caffeine.newBuilder()
                .expireAfterWrite(OTP_VALID_DURATION_MINUTES, TimeUnit.MINUTES) // OTP истекает через 5 минут
                .maximumSize(10_000) // Максимальное количество элементов в кэше
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

    private String generateOtp() {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000); // Генерируем 6-значный OTP
        return String.valueOf(otp);
    }
}