package com.medacare.backend.service;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.medacare.backend.config.ApplicationConfiguration;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.User;
import com.medacare.backend.model.User.UserOrigin;
import com.medacare.backend.repository.PatientRepository;
import com.medacare.backend.repository.UserRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final ResponseService responseService;
    private final ApplicationConfiguration applicationConfiguration;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final AI_AssistanceService aiAssistanceService;

    public PatientService(PatientRepository patientRepository, ResponseService responseService,
            ApplicationConfiguration applicationConfiguration, UserRepository userRepository,
            AuthenticationService authenticationService, AI_AssistanceService aiAssistanceService) {
        this.patientRepository = patientRepository;
        this.responseService = responseService;
        this.applicationConfiguration = applicationConfiguration;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.aiAssistanceService = aiAssistanceService;
    }

    public ResponseEntity<StandardResponse> savePatientDetail(Patient patient) {

        try {

            User user = userRepository.findById(authenticationService.getCurrentUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            patient.setUser(user);

            if (patientRepository.existsByUserId(authenticationService.getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse(
                        "error",
                        patient,
                        "Patient already registered",
                        null));
            }
            User patientUser = userRepository.findById(authenticationService.getCurrentUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            // patient.setUser(patientUser);
            Patient savedPatient = patientRepository.save(patient);
            user.setFirstLogin(false);
            userRepository.save(user);
            StandardResponse response = responseService.createStandardResponse("success", savedPatient,
                    "Patient registered successfully", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            StandardResponse response = responseService.createStandardResponse("error", null,
                    "Failed to register patient", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Patient getCurrentUserPatientProfile() {
        User currentUser = authenticationService.getCurrentUser();
        Patient patientProfile = patientRepository.findByUserId(currentUser.getId());
        if (patientProfile == null)
            throw new RuntimeException("Patient profile not found for user: ");
        return patientProfile;
    }

    public Patient createPatientData(User user, LocalDate dateOfBirth, String address, String contactNumber,
            String emergencyContactName, String emergencyContactNumber, String medicalHistory,
            String pastDiagnosis, String bloodType, String allergies, String medications,
            String preferredLanguage, String occupation, String maritalStatus, Double heightInMeters,
            Double weightInKg, String gender) {
        Patient patient = new Patient();
        User userFromDb = new User();

        userFromDb = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        userFromDb.setFirstLogin(false);
        patient.setUser(userFromDb);
        patient.setDateOfBirth(dateOfBirth);
        patient.setAddress(address);
        patient.setContactNumber(contactNumber);
        patient.setEmergencyContactName(emergencyContactName);
        patient.setEmergencyContactNumber(emergencyContactNumber);
        patient.setMedicalHistory(medicalHistory);
        patient.setPastDiagnosis(pastDiagnosis);
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        patient.setMedications(medications);
        patient.setPreferredLanguage(preferredLanguage);
        patient.setOccupation(occupation);
        patient.setMaritalStatus(maritalStatus);
        patient.setHeightInMeters(heightInMeters);
        patient.setWeightInKg(weightInKg);
        patient.setGender(gender);

        userRepository.save(userFromDb);
        String patientProfile = "The patient is " + patient.getAge() + " years old " + patient.getGender()
                + " and has the following medical history: "
                + patient.getMedicalHistory() + ". past diagnosis: "
                + patient.getPastDiagnosis() + ". blood type: " + patient.getBloodType();
        patient.setSpecializationPreference(aiAssistanceService.searchSpecialization(patientProfile));
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

}
