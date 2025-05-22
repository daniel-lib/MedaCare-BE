package com.medacare.backend.model.appointmentBooking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.medacare.backend.config.FixedVars;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Appointment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @CreationTimestamp
    private OffsetDateTime createdOn;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType;

    private String meetingDetails; // link or address

    private String meetingLink;

    // @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    private Patient patient;

    // @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Physician physician;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long institutionId;

    @Column(nullable = true)
    private BigDecimal serviceFee = new BigDecimal(0);
    @Column(nullable = true)
    private String serivceFeeCurrency;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private AvailabilitySlot availabilitySlot;

    @Column(nullable = false)
    private LocalTime appointmentStartTime;

    @Column(nullable = false)
    private LocalTime appointmentEndTime;

    @Column(nullable = false)
    private OffsetDateTime appointmentDateTz = FixedVars.DEFAULT_ZONED_DATE_TIME;

    // @Column(nullable = false)
    private LocalDate appointmentDate;

    public enum AppointmentStatus {
        SCHEDULED,
        COMPLETED,
        CANCELED,
        IN_PROGRESS,
        AWAITING_DOCUMENTATION,
    }

    public enum ConsultationType {
        VIRTUAL,
        IN_PERSON,
    }
}
