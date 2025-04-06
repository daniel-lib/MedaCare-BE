package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Appointment implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @CreationTimestamp
    private LocalDateTime createdOn;

    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType;

    private String meetingDetails;  // link or address

    @ManyToOne()
    private Patient patient;
    
    @ManyToOne()
    private Physician physician;

    public enum AppointmentStatus{
        SCHEDULED,
        COMPLETED,
        CANCELED,
        IN_PROGRESS,
    }
    public enum ConsultationType{
        VIRTUAL,
        IN_PERSON,
    }
}
