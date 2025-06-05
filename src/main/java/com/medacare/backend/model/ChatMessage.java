package com.medacare.backend.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;

    @CreationTimestamp
    private OffsetDateTime sentDate;
    @UpdateTimestamp
    private OffsetDateTime updateDate;

    private OffsetDateTime readDate;

    @Enumerated(EnumType.STRING)
    private MessageViewStatus viewStatus;

    public enum MessageViewStatus {
        UNREAD,
        SEEN,
        READ,
    }

}
