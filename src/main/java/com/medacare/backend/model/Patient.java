package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.medacare.backend.model.appointmentBooking.Appointment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Patient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private String contactNumber;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String medicalHistory;
    private String pastDiagnosis;
    private String bloodType;
    private String allergies;
    private String medications;
    private String preferredLanguage;
    private String occupation;
    private String maritalStatus;
    private Double heightInMeters;
    private Double weightInKg;
    private Double BMI;
    private String gender;

    private List<String> specializationPreference = new ArrayList<>(); 

    // @JsonManagedReference
    // @OneToMany(mappedBy = "patient")
    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "patient")
    private List<MedicalRecord> medicalRecord;

    public Double getBmi() {
        if (heightInMeters != null && weightInKg != null && heightInMeters > 0) {
            return weightInKg / (heightInMeters * heightInMeters);
        }
        return null;
    }

    public void setHeightInMeters(Double heightInMeters) {
        this.heightInMeters = heightInMeters;
        this.BMI = getBmi();
    }

    public Integer getAge() {
        if(dateOfBirth != null) {
            LocalDate today = LocalDate.now();
            int age = today.getYear() - dateOfBirth.getYear();
            if (today.getMonthValue() < dateOfBirth.getMonthValue() || 
                (today.getMonthValue() == dateOfBirth.getMonthValue() && today.getDayOfMonth() < dateOfBirth.getDayOfMonth())) {
                age--;
            }
            return age;
        } else {
            return null;
        }
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.age = getAge();
    }
}
