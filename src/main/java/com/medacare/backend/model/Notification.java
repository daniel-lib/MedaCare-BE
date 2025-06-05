package com.medacare.backend.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Notification implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String message;

    @CreationTimestamp
    private OffsetDateTime creationDate;

    @CreationTimestamp
    private OffsetDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @ManyToOne
    private User receiver;
    
    private Long referenceId;

    public enum NotificationStatus{
        SENT,
        PENDING,
        READ,
    }

}
