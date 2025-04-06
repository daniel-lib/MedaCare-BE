package com.medacare.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Physician physician;
    
    private String medication;
    private String instruction;
}
