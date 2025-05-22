package com.medacare.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.medacare.backend.model.appointmentBooking.Appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Physician physician;

    @ManyToOne
    private Institution institution;

    @ManyToOne
    private Appointment appointment;

    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private String status; // e.g., PENDING, PAID

    @Enumerated(EnumType.STRING)
    PayeeType payeeType;

    public enum PayeeType {
        INSTITUTION,
        PHYSICIAN
    }
}
