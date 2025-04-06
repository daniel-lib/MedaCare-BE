package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor  
public class HealthcareProvider implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // e.g. Hospital, Clinic, Private Practice
    private String address;
    private String phoneNumber;
    private String email;

    @OneToMany(mappedBy = "healthcareProvider")
    private List<Physician> physicians;

    //account that represents the providers
    @OneToOne
    @JoinColumn(name="admin_user_id", unique = true)
    private User adminUser;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
