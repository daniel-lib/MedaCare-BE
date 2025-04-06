package com.medacare.backend.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@PrimaryKeyJoinColumn(name="user_id")
public class Physician extends User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String specialization;
    private String licenseNumber;
    private String availabilitySchedule;    //eg. 01:00AM - 02:00PM

    private Boolean orgnanizationAffiliated;

    @ManyToOne()
    private HealthcareProvider healthcareProvider;

    
}
