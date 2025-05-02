package com.medacare.backend.service;

import java.util.List;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.medacare.backend.dto.AccountRequestRejectionReasonDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Physician.AccountRequestStatus;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.RoleRepository;
import com.medacare.backend.repository.UserRepository;

@Service
public class PhysicianService {

    private final PhysicianRepository physicianRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public PhysicianService(PhysicianRepository physicianRepository, InstitutionRepository institutionRepository,
            UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.physicianRepository = physicianRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<Physician> getAllPhysicians() {
        return physicianRepository.findByAccountRequestStatus(AccountRequestStatus.APPROVED);
    }

    public Physician getPhysicianById(Long id) {
        return physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
    }

    public Physician createPhysician(Physician physician) {
        return physicianRepository.save(physician);
    }

    public Physician updatePhysician(Long id, Physician physician) {
        Physician existingPhysician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));

        existingPhysician.setSpecialization(physician.getSpecialization());
        existingPhysician.setLicenseNumber(physician.getLicenseNumber());
        existingPhysician.setAvailabilitySchedule(physician.getAvailabilitySchedule());
        existingPhysician.setOrgnanizationAffiliated(physician.getOrgnanizationAffiliated());
        existingPhysician.setHealthcareProvider(physician.getHealthcareProvider());

        return physicianRepository.save(existingPhysician);
    }

    public void deletePhysician(Long id) {
        Physician existingPhysician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
        physicianRepository.delete(existingPhysician);
    }

    public List<Physician> getPhysiciansByHealthcareProvider(Long healthcareProviderId) {
        Institution institution = institutionRepository.findById(healthcareProviderId)
                .orElseThrow(
                        () -> new RuntimeException("Healthcare provider not found with id: " + healthcareProviderId));
        return physicianRepository.findByHealthcareProvider(institution);
    }

    public User createInstitutionPhysicianAccount(Physician physician, Institution institution) {
        if (userRepository.existsByEmail(physician.getEmail())) {
            throw new IllegalArgumentException("User account already exists for this email: " + physician.getEmail());
        }
        User user = new User();

        Random random = new Random();
        String passwordPrefix = physician.getFirstName().substring(0, 2).toUpperCase();
        String generatedPassword = passwordPrefix + "@m" + String.valueOf(random.nextInt(122_222_000, 999_999_999));
        user.setEmail(institution.getEmail());
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
        emailService.sendAutoAccountCreationEmail(user, generatedPassword,
                "An account has been created for you as a physician in the institution: " + institution.getName());
        return userRepository.save(user);
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

}
