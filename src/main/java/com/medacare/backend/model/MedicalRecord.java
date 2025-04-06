package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MedicalRecord implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String diagnosis;
    private String prescription;
    private String notes;

    @ManyToOne()
    private Physician physician;

    @ManyToOne()
    private Patient patient;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDate createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @UpdateTimestamp
    private LocalDate updatedOn;
    
}
