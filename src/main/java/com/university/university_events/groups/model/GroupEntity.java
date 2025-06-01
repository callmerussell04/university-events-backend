package com.university.university_events.groups.model;

import java.util.ArrayList;
import java.util.List;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.faculties.model.FacultyEntity;
import com.university.university_events.users.model.UserEntity;

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
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "groups")
public class GroupEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "facultyId")
    private FacultyEntity faculty;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> users = new ArrayList<>();

    public GroupEntity(String name, FacultyEntity faculty) {
        this.name = name;
        this.faculty = faculty;
    }
}