package com.medacare.backend.model.appointmentBooking;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medacare.backend.config.FixedVars;
import com.medacare.backend.model.Physician;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Data
@Setter
@Getter
@NoArgsConstructor
@Entity
public class AvailabilitySlot implements Serializable{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    private LocalDate date;
    private OffsetDateTime OffsetDateTime;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isBooked = false;


    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean onHold = false;

    @Column(nullable=false)
    private OffsetDateTime onHoldUntil = FixedVars.DEFAULT_ZONED_DATE_TIME;

    private Long userId;

    String paymentReference;
    String paymentRequestUrl;


    @JsonIgnore
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isMerged = false;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "availabilitySlot", orphanRemoval = true)
    private Appointment appointment;

    @JsonIgnore
    @ManyToOne(cascade=CascadeType.REMOVE)
    WorkingHoursWindow workingHoursWindow;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Physician physician;
}
