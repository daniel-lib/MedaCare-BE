package com.medacare.backend.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;
import com.medacare.backend.model.appointmentBooking.WorkingHoursWindow;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Physician implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String specialization;
    private String licenseNumber;
    // private String availabilitySchedule; // eg. 01:00AM - 02:00PM
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

    private boolean documentInvalid;
    private boolean licenseNotValid;
    private boolean identityUnverified;
    private boolean professionallyQualified;
    private String rejectionReasonNote = "";

    @Transient
    @JsonIgnore
    @Nullable
    private MultipartFile profilePhoto;

    @Transient
    @JsonIgnore
    @Nullable
    private MultipartFile nationalId;

    @Transient
    @JsonIgnore
    private MultipartFile resume;

    @Transient
    @JsonIgnore
    @Nullable
    private MultipartFile medicalLicense;

    @Transient
    @JsonIgnore
    @Nullable
    private MultipartFile specializationDoc;

    @Transient
    @JsonIgnore
    @Nullable
    private MultipartFile degreeCertificate;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String profilePhotoUrl = "https://res.cloudinary.com/db6j8ag7i/image/upload/v1746702635/default_physician_dgrfed.png";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nationalIdUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resumeUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String medicalLicenseUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String specializationUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String degreeCertificateUrl;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, mappedBy = "physician")
    private List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "physician")
    private List<Appointment> appointments = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "physician")
    private List<WorkingHoursWindow> workingHoursWindows = new ArrayList<>();

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
        return firstName != null ? firstName : (this.user != null ? this.user.getFirstName() : "_");
    }

    public String getLastName() {
        return lastName != null ? lastName : (this.user != null ? this.user.getLastName() : "_");
    }

    public String getEmail() {
        return email != null ? email : (this.user != null ? this.user.getEmail() : "_");
    }

    public enum AccountRequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

}
