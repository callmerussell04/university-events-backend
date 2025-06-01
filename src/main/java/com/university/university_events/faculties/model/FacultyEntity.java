package com.university.university_events.faculties.model;

import java.util.ArrayList;
import java.util.List;

import com.university.university_events.core.model.BaseEntity;
import com.university.university_events.groups.model.GroupEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "faculties")
public class FacultyEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEntity> groups = new ArrayList<>();

    public FacultyEntity(String name) {
        this.name = name;
    }
}