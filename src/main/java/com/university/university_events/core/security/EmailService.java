package com.university.university_events.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your One-Time Password (OTP)");
        message.setText("Hello,\n\nYour One-Time Password (OTP) for login is: " + otp + "\n\nThis code is valid for 5 minutes. Do not share it with anyone.\n\nThank you,\nYour University Events Team");
        mailSender.send(message);
    }
}
