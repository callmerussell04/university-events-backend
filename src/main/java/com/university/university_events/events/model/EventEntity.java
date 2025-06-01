package com.university.university_events.events.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.invitations.model.InvitationEntity;
import com.university.university_events.locations.model.LocationEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "events")
public class EventEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    @Column(nullable = false)
    private EventStatus status;
    @Column(nullable = false)
    private Date startDateTime;
    @Column(nullable = false)
    private Date endDateTime;
    @Column(nullable = false, length = 30)
    private String organizer;

    @ManyToOne
    @JoinColumn(name = "locationId")
    private LocationEntity location;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvitationEntity> invitations = new ArrayList<>();
}