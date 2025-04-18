package com.medacare.backend.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Physician implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String specialization;
    private String licenseNumber;
    private String availabilitySchedule;    //eg. 01:00AM - 02:00PM
    private String education;
    private String gender;

    private Boolean orgnanizationAffiliated;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    private HealthcareProvider healthcareProvider;

    
}
