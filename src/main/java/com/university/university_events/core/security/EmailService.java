package com.university.university_events.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("carrepairshoplab7@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Ваш одноразовый пароль (OTP) для входа");
            message.setText("Здравствуйте,\n\nВаш одноразовый пароль (OTP) для входа: " + otp + "\n\nЭтот код действителен в течение 5 минут. Не делитесь им ни с кем.");
            mailSender.send(message);
            logger.info("OTP email успешно отправлен на: {}", toEmail);
        } catch (MailException e) {
            logger.error("Ошибка при отправке OTP email на {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("carrepairshoplab7@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Сброс пароля - Ваш одноразовый пароль (OTP)");
            message.setText("Здравствуйте,\n\nВы запросили сброс пароля. Ваш одноразовый пароль (OTP) для сброса: " + otp + "\n\nЭтот код действителен в течение 5 минут. Пожалуйста, используйте его для установки нового пароля. Не делитесь им ни с кем.\n\nЕсли вы не запрашивали сброс пароля, проигнорируйте это письмо.");
            mailSender.send(message);
            logger.info("Password reset OTP email успешно отправлен на: {}", toEmail);
        } catch (MailException e) {
            logger.error("Ошибка при отправке OTP email для сброса пароля на {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendUsernameReminderEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("carrepairshoplab7@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Напоминание о вашем имени пользователя");
            message.setText("Здравствуйте,\n\nВы запросили напоминание о вашем имени пользователя.\n\nВаше имя пользователя: " + username + "\n\nЕсли вы не запрашивали это напоминание, проигнорируйте это письмо.\n\nСпасибо,\nКоманда University Events");
            mailSender.send(message);
            logger.info("Username reminder email успешно отправлен на: {}", toEmail);
        } catch (MailException e) {
            logger.error("Ошибка при отправке напоминания об имени пользователя на {}: {}", toEmail, e.getMessage());
        }
    }
}
