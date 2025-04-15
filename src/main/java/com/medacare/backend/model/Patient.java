package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@PrimaryKeyJoinColumn(name  ="user_id")
public class Patient extends User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate dateOfBirth;
    private String address;
    private String contactNumber;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String personalHistory;
    private String pastDiagnosis;
    private String bloodType;
    private String allergies;
    private String medications;
    private String insuranceProvider;
    private String insurancePolicyNumber;
    private String preferredLanguage;
    private String occupation;

    @OneToMany(mappedBy = "patient")
    private List<MedicalRecord> medicalRecord;
}
