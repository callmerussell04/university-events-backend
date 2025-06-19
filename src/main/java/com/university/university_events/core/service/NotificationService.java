package com.university.university_events.core.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.invitations.repository.InvitationRepository;
import com.university.university_events.users.model.UserEntity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final InvitationRepository invitationRepository;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    public NotificationService(InvitationRepository invitationRepository, RestTemplate restTemplate) {
        this.invitationRepository = invitationRepository;
        this.restTemplate = restTemplate; 
    }

    private void sendFirebaseNotification(String deviceToken, String title, String body, String eventId, String eventType) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(deviceToken)
                .putData("eventId", eventId)
                .putData("eventType", eventType)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent Firebase message to token " + deviceToken + ": " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send Firebase message to token " + deviceToken + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendTelegramMessage(String chatId, String messageText) {
        if (telegramBotToken == null || telegramBotToken.isEmpty()) {
            System.err.println("Telegram bot token is not configured. Cannot send Telegram message.");
            return;
        }

        String telegramApiUrl = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", messageText);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(telegramApiUrl, request, String.class);
            System.out.println("Telegram notification sent to chat " + chatId);
        } catch (Exception e) {
            System.err.println("Error sending Telegram notification to chat " + chatId + ": " + e.getMessage());
        }
    }
    
    public void sendEventStatusChangeNotification(EventEntity event) {
        String title = "Обновление статуса мероприятия: " + event.getName();
        String body = "";

        switch (event.getStatus()) {
            case ACTIVE:
                body = "Мероприятие '" + event.getName() + "' началось. Присоединяйтесь!";
                break;
            case CANCELED:
                body = "Мероприятие '" + event.getName() + "' было отменено. Приносим извинения.";
                break;
            default:
                return;
        }

        List<InvitationEntity> invitations = invitationRepository.findByEventId(event.getId());

        for (InvitationEntity invitation : invitations) {
            UserEntity user = invitation.getUser();
            if (user == null) {
                System.out.println("Skipping invitation due to null user.");
                continue;
            }

            if (user.getDeviceToken() != null && !user.getDeviceToken().isEmpty()) {
                sendFirebaseNotification(user.getDeviceToken(), title, body, String.valueOf(event.getId()), event.getStatus().name());
            } else {
                System.out.println("User " + user.getUsername() + " has no Firebase device token.");
            }

            if (user.getTelegramChatId() != null && !user.getTelegramChatId().isEmpty()) {
                sendTelegramMessage(user.getTelegramChatId(), body);
            } else {
                System.out.println("User " + user.getUsername() + " has no Telegram Chat ID.");
            }
        }
    }
}