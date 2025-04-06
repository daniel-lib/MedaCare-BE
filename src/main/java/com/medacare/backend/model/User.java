package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "app_user")
@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @ManyToOne()
    @JoinColumn(name = "user_group")
    private UserGroup userGroup;

    // private String phoneNumber;
    private String gender;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDate createdOn;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @UpdateTimestamp
    private LocalDate updatedOn;

    private boolean active = true;
}
