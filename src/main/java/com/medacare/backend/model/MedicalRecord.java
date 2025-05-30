package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.medacare.backend.model.appointmentBooking.Appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class MedicalRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String diagnosis;
    private String differentialDiagnosis; // possibes diagnosis
    private String treatment; // treatment given to the patient
    private String prescription;
    private String notes;

    @ManyToOne()
    private Physician physician;

    @ManyToOne()
    private Patient patient;

    @CreationTimestamp
    private OffsetDateTime createdAt;
    @CreationTimestamp
    private LocalDate createdOn;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
    @UpdateTimestamp
    private LocalDate updatedOn;

    @OneToOne
    Appointment appointment;

    private LocalDate visitDate;
    private String visitReason;
    private String treatmentPlan;
    private String followUpInstructions;
    private String allergies;
    private String medicalHistory;
    private String labResults;
    private Double totalCost;
    private String paymentStatus;
    private String status;
    private String createdBy;
    private String examinationFindings;
    private String assessment; // physician’s analysis before diagnosis — distinct from lab results or
                               // treatment
    private boolean consentGiven = false; // whether the patient has given consent for treatment
    private OffsetDateTime consentDate; // date of consent
    private boolean isFollowUp = false; // whether the appointment is a follow-up
    private String followUpDate; // date of the follow-up appointment

    public enum RecordStatus {
        DRAFT,
        FINALIZED,
        ARCHIVED,
        DELETED
    }
}
