// packages/com/university/university_events/notifications/NotificationService.java
package com.university.university_events.core.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.university.university_events.events.model.EventEntity;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.invitations.repository.InvitationRepository; // Предполагается, что у вас есть такой репозиторий
import com.university.university_events.users.model.UserEntity;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final InvitationRepository invitationRepository; // Чтобы получить токены устройств пользователей

    public NotificationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public void sendEventStatusChangeNotification(EventEntity event) {
        String title = "Обновление статуса мероприятия: " + event.getName();
        String body = "";

        switch (event.getStatus()) {
            case ACTIVE:
                body = "Мероприятие '" + event.getName() + "' теперь АКТИВНО. Присоединяйтесь!";
                break;
            case CANCELED:
                body = "Мероприятие '" + event.getName() + "' было ОТМЕНЕНО. Приносим извинения.";
                break;
            default:
                // Не отправляем уведомления для других статусов, если это не требуется
                return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Получаем токены устройств всех, кто приглашен на это мероприятие
        List<InvitationEntity> invitations = invitationRepository.findByEventId(event.getId());
        List<String> registrationTokens = invitations.stream()
                .map(InvitationEntity::getUser) // Получаем UserEntity из InvitationEntity
                .filter(user -> user != null && user.getDeviceToken() != null) // Фильтруем, если пользователь или токен отсутствуют
                .map(UserEntity::getDeviceToken) // Получаем deviceToken
                .collect(Collectors.toList());

        if (registrationTokens.isEmpty()) {
            System.out.println("No device tokens found for event " + event.getName());
            return;
        }

        // Отправка уведомлений
        for (String token : registrationTokens) {
            Message message = Message.builder()
                    .setNotification(notification)
                    .setToken(token)
                    .putData("eventId", String.valueOf(event.getId())) // Дополнительные данные, которые можно использовать в приложении
                    .putData("eventType", event.getStatus().name())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent message to token " + token + ": " + response);
            } catch (FirebaseMessagingException e) {
                System.err.println("Failed to send message to token " + token + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}