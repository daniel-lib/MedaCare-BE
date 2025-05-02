package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    private String availabilitySchedule; // eg. 01:00AM - 02:00PM
    private String education;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private Integer experience; // in years
    private String languagesSpoken;

    private double rating = 0.0d;

    @Transient
    @NotBlank(message = "Email address is mandatory")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email address")
    private String email;

    @Size(min = 3, max = 100, message = "First name must be between 3 and 100 characters")
    @NotBlank(message = "First name is required")
    @Transient
    private String firstName;
    @Size(min = 3, max = 100, message = "Last name must be between 3 and 100 characters")
    @NotBlank(message = "Last name is required")
    @Transient
    private String lastName;

    @Pattern(regexp = "^\\+251\\d{9}$", message = "Invalid phone number")
    private String phoneNumber;

    private Boolean orgnanizationAffiliated;

    @Enumerated(EnumType.STRING)
    private AccountRequestStatus accountRequestStatus = AccountRequestStatus.PENDING;

    @JsonIgnore
    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    private Institution healthcareProvider;

    private List<String> fileUploads; // Urls
    private List<Long> fileUploadsReference; // Ids

    private boolean documentInvalid;
    private boolean licenseNotValid;
    private boolean identityUnverified;
    private boolean professionallyQualified;
    private String rejectionReasonNote;

    public Integer getAge() {
        if (dateOfBirth != null) {
            LocalDate today = LocalDate.now();
            int age = today.getYear() - dateOfBirth.getYear();
            if (today.getMonthValue() < dateOfBirth.getMonthValue() ||
                    (today.getMonthValue() == dateOfBirth.getMonthValue()
                            && today.getDayOfMonth() < dateOfBirth.getDayOfMonth())) {
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

    public String getFirstName() {
        return this.user != null ? this.user.getFirstName() : null;
    }

    public String getLastName() {
        return this.user != null ? this.user.getLastName() : null;
    }

    public enum AccountRequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

}
