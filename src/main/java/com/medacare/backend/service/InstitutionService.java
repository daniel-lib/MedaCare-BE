package com.medacare.backend.service;

import org.springframework.web.server.ResponseStatusException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.User;
import com.medacare.backend.model.helper.InstitutionFile;
import com.medacare.backend.repository.InstitutionFileRepository;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.RoleRepository;
import com.medacare.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service

public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstitutionFileRepository institutionFileRepository;
    private final AuthenticationService authenticationService;
    private final PhysicianService physicianService;

    public InstitutionService(InstitutionRepository institutionRepository, EmailService emailService,
            RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
            InstitutionFileRepository institutionFileRepository, AuthenticationService authenticationService,
            PhysicianService physicianService) {
        this.authenticationService = authenticationService;
        this.institutionFileRepository = institutionFileRepository;
        this.roleRepository = roleRepository;
        this.institutionRepository = institutionRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.physicianService = physicianService;
    }

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findByRequestStatus(InstitutionRegistrationRequestStatus.APPROVED);
    }

    public Institution getInstitutionById(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
    }

    @Transactional
    public Institution createInstitution(@Valid Institution institution) {

        institution.setRequestStatus(InstitutionRegistrationRequestStatus.PENDING);
        Institution savedInsitution = institutionRepository.save(institution);

        if (institution.getFileUploads() != null && !institution.getFileUploads().isEmpty()) {
            try {
                for (var file : institution.getFileUploads().entrySet()) {
                    InstitutionFile institutionFile = new InstitutionFile();
                    institutionFile.setFileOwner(savedInsitution);
                    institutionFile.setFileCategory(file.getKey());
                    institutionFile
                            .setFileDescription("Business Verification Document for " + savedInsitution.getName());
                    institutionFile.setFileURL(file.getValue());
                    institutionFileRepository.save(institutionFile);
                }
            } catch (Exception exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File upload failed", exception);
            }
        }
        return savedInsitution;
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
        if (userRepository.existsByEmail(institution.getEmail())) {
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
        user.setFirstName(institution.getName());
        user.setLastName("Admin");
        emailService.sendAutoAccountCreationEmail(user, generatedPassword, "We received a request to create account for your institution");
        return userRepository.save(user);
    }

    public Institution rejectInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
        institution.setRequestStatus(InstitutionRegistrationRequestStatus.REJECTED);
        return institutionRepository.save(institution);
    }

    public Institution getCurrentInstitution() {
        User institutionAdmin = authenticationService.getCurrentUser();
        return institutionRepository.findById(institutionAdmin.getInstitutionId())
                .orElseThrow(() -> new RuntimeException(
                        "Institution not found with id: " + institutionAdmin.getInstitutionId()));
    }

    public List<Physician> getInstitutionPhysicians() {
        if(authenticationService.getCurrentUser().getInstitutionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User is not associated with any institution.");
        }
        
        long institutionId = authenticationService.getCurrentUser().getInstitutionId();
        List<Physician> institutionPhysician = physicianService.getPhysiciansByHealthcareProvider(institutionId);
        return institutionPhysician;
    }
}
