package com.medacare.backend.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medacare.backend.model.helper.InstitutionFile;
import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Institution implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Basic Info
    @NotBlank(message = "Name is mandatory")
    @NotNull(message = "Name is mandatory")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    private String type; // Enum: HOSPITAL, CLINIC, DIAGNOSTIC_CENTER, etc.

    // Location Info
    // @NotBlank(message = "Country is mandatory")
    private String country = "Ethiopia"; // Default to Ethiopia for now
    private String regionOrState;
    private String subCityOrDistrict;
    private String street;

    // Legal & Operational Info
    private String registrationLicenseNumber;
    private Integer yearEstablished;
    private String aboutInstitution;
    private Double rating = 0.0;

    // Contact Info
    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
    private String email; // Used for notifications and follow-ups

    private String primaryContactPersonName;

    private String primaryContactPersonRole;

    // Services & Facilities
    private String offeredServices; // Comma-separated string
    private String availableFacilities; // e.g., lab testing, pharmacy
    private String offeredSpecializations; // e.g., Cardiology, Dermatology

    @JsonIgnore
    @OneToMany(mappedBy = "healthcareProvider")
    private List<Physician> physicians;

    // account that represents the providers
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "admin_user_id", unique = true)
    private User adminUser;

    @CreationTimestamp
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private InstitutionRegistrationRequestStatus requestStatus;

    private boolean documentInvalid;
    private boolean licenseNotValid;
    private boolean identityUnverified;
    private boolean professionallyQualified;
    private String rejectionReasonNote = "";

    @Transient
    @JsonIgnore
    private MultipartFile businessDocument;

    private String businessDocumentUrl;

    @Transient
    @JsonIgnore
    private MultipartFile medicalLicense;
    
    private String medicalLicenseUrl;

    private String companyLogo = "https://res.cloudinary.com/db6j8ag7i/image/upload/v1746702635/institutionLogoDefault_gwmg9o.png";



    @JsonIgnore
    @OneToMany(mappedBy = "fileOwner")
    private List<InstitutionFile> uploadedFiles;

    public enum InstitutionRegistrationRequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
