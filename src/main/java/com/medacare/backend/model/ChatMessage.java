package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;

    @CreationTimestamp
    private LocalDateTime sentDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;

    private LocalDateTime readDate;

    @Enumerated(EnumType.STRING)
    private MessageViewStatus viewStatus;

    public enum MessageViewStatus {
        UNREAD,
        SEEN,
        READ,
    }

}
