package com.university.university_events.events.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStatisticsDto {
    private Long id;
    private String name;
    private String status;
    private String startDateTime;
    private String endDateTime;
    private String organizer;
    private String locationName;
    private int invitedCount;
    private int attendedCount;
    private int notAttendedCount;
}
