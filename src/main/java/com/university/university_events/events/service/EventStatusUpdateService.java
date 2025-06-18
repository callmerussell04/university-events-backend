// packages/com/university/university_events/events/service/EventStatusUpdateService.java
package com.university.university_events.events.service;

import com.university.university_events.events.model.EventEntity;
import com.university.university_events.events.model.EventStatus;
import com.university.university_events.events.repository.EventRepository;
import com.university.university_events.core.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EventStatusUpdateService {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    public EventStatusUpdateService(EventRepository eventRepository, NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 60000)
    public void updateEventStatuses() {
        Date now = new Date();

        List<EventEntity> plannedEvents = eventRepository.findByStatusAndStartDateTimeBefore(EventStatus.PLANNED, now);
        plannedEvents.forEach(event -> {
            if (event.getEndDateTime().after(now)) {
                event.setStatus(EventStatus.ACTIVE);
                eventRepository.save(event);
                System.out.println("Event " + event.getName() + " is now ACTIVE.");
                notificationService.sendEventStatusChangeNotification(event);
            }
        });

        List<EventEntity> activeAndPlannedEvents = eventRepository.findByStatusInAndEndDateTimeBefore(
                List.of(EventStatus.ACTIVE, EventStatus.PLANNED), now);
        activeAndPlannedEvents.forEach(event -> {
            if (event.getStatus() != EventStatus.COMPLETED) {
                event.setStatus(EventStatus.COMPLETED);
                eventRepository.save(event);
                System.out.println("Event " + event.getName() + " is now COMPLETED.");
            }
        });
    }
}