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
public class Notification implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String message;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @CreationTimestamp
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @ManyToOne
    private User receiver;

    public enum NotificationStatus{
        SENT,
        PENDING,
        READ,
    }

}
