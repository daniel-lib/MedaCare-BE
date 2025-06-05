package com.medacare.backend.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.medacare.backend.dto.AccountRequestRejectionReasonDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Physician.AccountRequestStatus;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;
import com.medacare.backend.model.appointmentBooking.Calendar;
import com.medacare.backend.model.appointmentBooking.WorkingHoursWindow;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.RoleRepository;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.repository.appointment.AppointmentRepository;
import com.medacare.backend.repository.appointment.AvailabilitySlotRepository;
import com.medacare.backend.repository.appointment.WorkingHoursWindowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhysicianService {

    private final PhysicianRepository physicianRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ResponseService responseService;
    private final CloudinaryFileUploadService fileUploadService;
    private final AuthenticationService authenticationService;
    private final AppointmentService appointmentService;
    private final WorkingHoursWindowRepository workingHoursWindowRepository;
    private final AppointmentRepository appointmentRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final InstitutionService institutionService;

    public List<Physician> getApprovedPhysicians() {
        return physicianRepository.findByAccountRequestStatus(AccountRequestStatus.APPROVED);
    }

    public List<Physician> getAllPhysicians() {
        return physicianRepository.findAll();
    }

    public Physician getPhysicianById(Long id) {
        return physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
    }

    public ResponseEntity<StandardResponse> createPhysician(Physician physician) throws IOException {
        Long userId = authenticationService.getCurrentUser().getId();

        User physicianUser = authenticationService.getCurrentUser();
        physician.setUser(physicianUser);

        if (physicianRepository.existsByUserId(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Physician info already saved", null));
        }
        if (physician.getDateOfBirth().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Date of birth cannot be in the future", null));
        }

        // Upload files to Cloudinary and save URLs
        if (fileUploadService.isValidPhoto(physician.getProfilePhoto())) {
            physician.setProfilePhotoUrl(
                    fileUploadService.uploadFile(physician.getProfilePhoto()));
        }

        if (!fileUploadService.fileIsValid(physician.getNationalId()) ||
                !fileUploadService.fileIsValid(physician.getResume()) ||
                !fileUploadService.fileIsValid(physician.getMedicalLicense()) ||
                !fileUploadService.fileIsValid(physician.getSpecializationDoc()) ||
                !fileUploadService.fileIsValid(physician.getDegreeCertificate())) {
            throw new RuntimeException("One or more required files are invalid or missing.");
        }
        physician.setNationalIdUrl(fileUploadService.uploadFile(physician.getNationalId()));
        physician.setResumeUrl(fileUploadService.uploadFile(physician.getResume()));
        physician.setMedicalLicenseUrl(fileUploadService.uploadFile(physician.getMedicalLicense()));
        physician.setSpecializationUrl(fileUploadService.uploadFile(physician.getSpecializationDoc()));
        physician.setDegreeCertificateUrl(fileUploadService.uploadFile(physician.getDegreeCertificate()));

        physicianUser.setFirstLogin(false);

        Physician createdPhysician = physicianRepository.save(physician);
        userRepository.save(physicianUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.createStandardResponse("success", createdPhysician,
                        "Physician profile has been submitted. Awaiting review.", null));
    }

    public Physician updatePhysician(Long id, Physician physician) {
        Physician existingPhysician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));

        existingPhysician.setSpecialization(physician.getSpecialization());
        existingPhysician.setLicenseNumber(physician.getLicenseNumber());
        existingPhysician.setOrgnanizationAffiliated(physician.getOrgnanizationAffiliated());
        existingPhysician.setHealthcareProvider(physician.getHealthcareProvider());

        return physicianRepository.save(existingPhysician);
    }

    public void deletePhysician(Long id) {
        Physician existingPhysician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
        physicianRepository.delete(existingPhysician);
    }

   

    public User createInstitutionPhysicianAccount(Physician physician, Institution institution) {

        User user = new User();

        Random random = new Random();
        String passwordPrefix = physician.getFirstName().substring(0, 2).toUpperCase();
        String generatedPassword = passwordPrefix + "@m" + String.valueOf(random.nextInt(122_222_000, 999_999_999));
        user.setEmail(physician.getEmail());
        user.setPassword(passwordEncoder.encode(generatedPassword));
        Role physicianRole = roleRepository.findByName(RoleEnum.PHYSICIAN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(physicianRole);
        user.setInstitutionId(institution.getId());
        user.setInstitutionName(institution.getName());
        user.setPasswordResetRequired(true);
        user.setFirstName(physician.getFirstName());
        user.setLastName(physician.getLastName());
        user.setFirstLogin(false);
        user.setOrigin(User.UserOrigin.ORGANIZATION_CREATED);
        emailService.sendAutoAccountCreationEmail(user, generatedPassword,
                "An account has been created for you as a physician in the institution: " + institution.getName());
        return userRepository.save(user);
    }

    public ResponseEntity<StandardResponse> addInstitutionPhysician(Physician physician) throws IOException {

        if (physician.getDateOfBirth().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Date of birth cannot be in the future", null));
        }

        Institution institution = institutionService.getCurrentInstitution();

        if (physician.getEmail() == null || physician.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email address is mandatory");
        }
        if (userRepository.existsByEmailIgnoreCase(physician.getEmail())) {
            throw new IllegalArgumentException("User account already exists for this email: " + physician.getEmail());
        }

        // Upload files to Cloudinary and save URLs
        if (fileUploadService.isValidPhoto(physician.getProfilePhoto())) {
            physician.setProfilePhotoUrl(
                    fileUploadService.uploadFile(physician.getProfilePhoto()));
        }

        if (!fileUploadService.fileIsValid(physician.getNationalId()) ||
                !fileUploadService.fileIsValid(physician.getResume()) ||
                !fileUploadService.fileIsValid(physician.getMedicalLicense()) ||
                !fileUploadService.fileIsValid(physician.getSpecializationDoc()) ||
                !fileUploadService.fileIsValid(physician.getDegreeCertificate())) {
            throw new RuntimeException("One or more required files are invalid or missing.");
        }
        physician.setNationalIdUrl(fileUploadService.uploadFile(physician.getNationalId()));
        physician.setResumeUrl(fileUploadService.uploadFile(physician.getResume()));
        physician.setMedicalLicenseUrl(fileUploadService.uploadFile(physician.getMedicalLicense()));
        physician.setSpecializationUrl(fileUploadService.uploadFile(physician.getSpecializationDoc()));
        physician.setDegreeCertificateUrl(fileUploadService.uploadFile(physician.getDegreeCertificate()));

        physician.setHealthcareProvider(institution);
        physician.setOrgnanizationAffiliated(true);
        physician.setAccountRequestStatus(AccountRequestStatus.APPROVED);
        Physician createdPhysician = physicianRepository.save(physician);

        User user = this.createInstitutionPhysicianAccount(physician, institution);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.createStandardResponse("error", null,
                            "Failed to create user account for physician", null));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.createStandardResponse("success", null,
                        "Physician " + user.getFirstName() + " " + user.getLastName()
                                + " has been added to institution.",
                        null));
    }

    public List<Physician> getAllPendingPhysician() {
        return physicianRepository.findByAccountRequestStatus(AccountRequestStatus.PENDING);
    }

    public Physician approvepPhysician(Long id) {
        Physician physician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
        physician.setAccountRequestStatus(AccountRequestStatus.APPROVED);
        return physicianRepository.save(physician);
    }

    public Physician rejectPhysician(Long id, AccountRequestRejectionReasonDto rejectionReason) {
        Physician physician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
        physician.setAccountRequestStatus(AccountRequestStatus.REJECTED);
        physician.setDocumentInvalid(rejectionReason.isDocumentInvalid());
        physician.setLicenseNotValid(rejectionReason.isLicenseNotValid());
        physician.setIdentityUnverified(rejectionReason.isIdentityUnverified());
        physician.setRejectionReasonNote(rejectionReason.getRejectionReasonNote());
        return physicianRepository.save(physician);
    }

    public ResponseEntity<StandardResponse> createWorkingHoursWindow(@RequestBody WorkingHoursWindow workWindow) {
        return appointmentService.createWorkHourWindows(workWindow);
    }

    public ResponseEntity<StandardResponse> getWorkingHoursWindow(Physician physician) {
        List<WorkingHoursWindow> workWindow = workingHoursWindowRepository.findByPhysician(physician);
        return ResponseEntity.ok().body(
                responseService.createStandardResponse("sucess", workWindow, "Availability schedule fetched", null));
    }

    @Transactional
    public ResponseEntity<StandardResponse> deleteWorkingHoursWindow(long workingHoursWindowId) {
        WorkingHoursWindow workWindow = workingHoursWindowRepository.findById(workingHoursWindowId)
                .orElseThrow(
                        () -> new RuntimeException("Working hours window not found with id: " + workingHoursWindowId));

        if (workWindow.getPhysician().getUser().getId() != authenticationService.getCurrentUser().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(responseService.createStandardResponse("error", null,
                            "You are not authorized to delete this working hours window", null));
        }

        if (workWindow.getDate().isEqual(LocalDate.now()) || workWindow.getDate().isAfter(LocalDate.now())) {
            List<AvailabilitySlot> availabilitySlots = workWindow.getAvailabilitySlot();
            for (AvailabilitySlot availabilitySlot : availabilitySlots) {
                if (availabilitySlot.isBooked() &&
                        availabilitySlot.getOffsetDateTime()
                                .isBefore(OffsetDateTime.now(ZoneOffset.UTC).minusHours(6))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(responseService.createStandardResponse("error", null,
                                    "Cannot delete a working hours window that has booked slots within the next 6 hours",
                                    null));
                }
            }
        }

        workingHoursWindowRepository.delete(workWindow);

        return ResponseEntity.ok().body(
                responseService.createStandardResponse("success", null, "Availability schedule deleted", null));
    }

    public List<Appointment> getPhysicianAppointment(Physician physician) {
        return appointmentService.findPhysicianAppointment(physician);
    }

}
