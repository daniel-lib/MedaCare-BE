package com.medacare.backend.service;

import org.springframework.web.server.ResponseStatusException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.medacare.backend.dto.AccountRequestRejectionReasonDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.User;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.model.helper.InstitutionFile;
import com.medacare.backend.repository.InstitutionFileRepository;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.RoleRepository;
import com.medacare.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final ResponseService responseService;
    private final CloudinaryFileUploadService fileUploadService;
    private final AppointmentService appointmentService;
    private final InstitutionPhysicianService institutionPhysicianService;

    public Institution getInstitutionById(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
    }

    @Transactional
    public ResponseEntity<StandardResponse> createInstitution(@Valid Institution institution) throws IOException {

        if (!fileUploadService.isValidDocument(institution.getMedicalLicense())
                && !fileUploadService.isValidDocument(institution.getBusinessDocument())) {
            throw new RuntimeException("One or more required file is missing or invalid file type");
        }

        try {
            institution.setMedicalLicenseUrl(fileUploadService.uploadFile(institution.getMedicalLicense()));
            institution.setBusinessDocumentUrl(fileUploadService.uploadFile(institution.getBusinessDocument()));
        } catch (Exception e) {
            throw new IOException("One or more required file is missing or invalid");
        }

        institution.setRequestStatus(InstitutionRegistrationRequestStatus.PENDING);
        Institution savedInsitution = institutionRepository.save(institution);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.createStandardResponse("success", savedInsitution,
                        "Institution registration request submitted successfully", null));
    }

    public Institution updateInstitution(Long id, Institution institution) {
        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));

        existingInstitution.setName(institution.getName());
        existingInstitution.setType(institution.getType());
        existingInstitution.setCountry(institution.getCountry());
        existingInstitution.setRegionOrState(institution.getRegionOrState());
        existingInstitution.setSubCityOrDistrict(institution.getSubCityOrDistrict());
        existingInstitution.setStreet(institution.getStreet());
        existingInstitution.setRegistrationLicenseNumber(institution.getRegistrationLicenseNumber());
        existingInstitution.setYearEstablished(institution.getYearEstablished());
        existingInstitution.setAboutInstitution(institution.getAboutInstitution());
        existingInstitution.setEmail(institution.getEmail());
        existingInstitution.setPrimaryContactPersonName(institution.getPrimaryContactPersonName());
        existingInstitution.setPrimaryContactPersonRole(institution.getPrimaryContactPersonRole());
        existingInstitution.setOfferedServices(institution.getOfferedServices());
        existingInstitution.setAvailableFacilities(institution.getAvailableFacilities());
        existingInstitution.setOfferedSpecializations(institution.getOfferedSpecializations());

        return institutionRepository.save(existingInstitution);
    }

    public void deleteInstitution(Long id) {
        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
        institutionRepository.delete(existingInstitution);
    }

    public List<Institution> getAllApprovedInstitutions() {
        return institutionRepository.findByRequestStatus(InstitutionRegistrationRequestStatus.APPROVED);
    }

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public List<Institution> getAllPendingInstitutions() {
        return institutionRepository.findByRequestStatus(InstitutionRegistrationRequestStatus.PENDING);
    }

    public List<Institution> getAllRejectedInstitutions() {
        return institutionRepository.findByRequestStatus(InstitutionRegistrationRequestStatus.REJECTED);
    }

    public Institution approveInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
        institution.setRequestStatus(InstitutionRegistrationRequestStatus.APPROVED);

        createInstitutionAccount(institution);
        return institutionRepository.save(institution);
    }

    public User createInstitutionAccount(Institution institution) {
        if (userRepository.existsByEmailIgnoreCase(institution.getEmail())) {
            throw new IllegalArgumentException("User account already exists for this email: " + institution.getEmail());
        }
        User user = new User();

        Random random = new Random();
        String passwordPrefix = institution.getName().substring(0, 2).toUpperCase();
        String generatedPassword = passwordPrefix + "@m" + String.valueOf(random.nextInt(122_222_000, 999_999_999));
        user.setEmail(institution.getEmail());
        user.setPassword(passwordEncoder.encode(generatedPassword));
        Role orgAdminRole = roleRepository.findByName(RoleEnum.ORG_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(orgAdminRole);
        user.setInstitutionId(institution.getId());
        user.setInstitutionName(institution.getName());
        user.setPasswordResetRequired(true);
        user.setVerified(true);
        user.setFirstName(institution.getName());
        user.setLastName("Admin");
        user.setOrigin(User.UserOrigin.ORGANIZATION_CREATED);
        User createdUser = userRepository.save(user);
        institution.setAdminUser(createdUser);
        emailService.sendAutoAccountCreationEmail(createdUser, generatedPassword,
                "We received a request to create account for your institution");
        return createdUser;
    }

    public Institution rejectInstitution(Long id, AccountRequestRejectionReasonDto rejectionReason) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
        institution.setRequestStatus(InstitutionRegistrationRequestStatus.REJECTED);
        institution.setDocumentInvalid(rejectionReason.isDocumentInvalid());
        institution.setLicenseNotValid(rejectionReason.isLicenseNotValid());
        institution.setIdentityUnverified(rejectionReason.isIdentityUnverified());
        institution.setRejectionReasonNote(rejectionReason.getRejectionReasonNote());
        return institutionRepository.save(institution);
    }

    public Institution getCurrentInstitution() {
        User institutionAdmin = authenticationService.getCurrentUser();
        if(institutionAdmin.getInstitutionId() == null) {
            throw new RuntimeException("User is not associated with any institution.");
        }
        return institutionRepository.findById(institutionAdmin.getInstitutionId())
                .orElseThrow(() -> new RuntimeException(
                        "Institution not found"));
    }

    public List<Physician> getInstitutionPhysicians() {
        Long institutionId = authenticationService.getCurrentUser().getInstitutionId();
        if (institutionId == null) {
            throw new RuntimeException("User is not associated with any institution.");
        }

        return institutionPhysicianService.getInstitutionPhysiciansById(institutionId);
    }


    public List<Patient> getInstitutionPatients() {
        Long institutionId = authenticationService.getCurrentUser().getInstitutionId();
        if (institutionId == null) {
            throw new RuntimeException("User is not associated with any institution.");
        }
        return getInstitutionPatientsByInstitutionId(institutionId);
    }
    public List<Patient> getInstitutionPatientsByInstitutionId(Long institutionId) {
        if (institutionId == null) {
            throw new RuntimeException("User is not associated with any institution.");
        }
        // get institution physicians
        List<Physician> institutionPhysician = this.getInstitutionPhysicians();
        // getAppointment of those physicians
        List<Appointment> institutionAppointment = appointmentService.getAppointmentsByPhysicians(institutionPhysician);
        Set<Patient> institutionPatientSet = new HashSet<>();
        institutionAppointment.forEach(appointment -> {
            institutionPatientSet.add(appointment.getPatient());
        });
        List<Patient> institutionPatient = institutionPatientSet.stream().toList();

        return institutionPatient;

}
}
