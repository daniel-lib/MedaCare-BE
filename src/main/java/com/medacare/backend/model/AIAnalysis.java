package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="ai_analysis")
public class AIAnalysis implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    // private byte[] uploadedImage;   //file reference
    private String uploadedImageName;
    private String prompt;
    private String analysisResult;


    @Enumerated(EnumType.STRING)
    private AIAnalysisScope scope;

    @Nullable
    private Long medicalRecordRef;

    @CreationTimestamp
    public LocalDateTime createdAt;
    @UpdateTimestamp
    public LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "patient")
    private Patient patient;

    @ManyToMany
    @JoinTable(name = "medical_record_AI_Analysis", joinColumns = @JoinColumn(name = "ai_analysis"),
    inverseJoinColumns = @JoinColumn(name="medica_record"))
    private List<MedicalRecord> medicalRecord;

    public enum AIAnalysisScope{
        MEDICAL_RECORD, //one session(record)
        MEDICAL_HISTORY //overall history
    }
}