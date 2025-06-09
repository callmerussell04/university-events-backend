package com.university.university_events.users.model;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.groups.model.GroupEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true, length = 30)
    private String username;
    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;
    @Column(nullable = false, length = 60)
    private String password;
    @Column(nullable = false)
    private UserRole role;
    @ManyToOne
    @JoinColumn(name = "groupId")
    private GroupEntity group;
}