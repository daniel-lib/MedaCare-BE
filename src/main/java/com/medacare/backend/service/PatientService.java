package com.medacare.backend.service;

import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.medacare.backend.config.ApplicationConfiguration;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.PatientRepository;
import com.medacare.backend.repository.UserRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final ResponseService responseService;
    private final ApplicationConfiguration applicationConfiguration;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, ResponseService responseService,
            ApplicationConfiguration applicationConfiguration, UserRepository userRepository,
            AuthenticationService authenticationService) {
        this.patientRepository = patientRepository;
        this.responseService = responseService;
        this.applicationConfiguration = applicationConfiguration;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public ResponseEntity<StandardResponse> savePatientDetail(Patient patient) {
        try {
            if (patientRepository.existsByUserId(authenticationService.getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse(
                        "error",
                        patient,
                        "Patient already registered",
                        null));
            }
            patient.setUser(authenticationService.getCurrentUser());
            Patient savedPatient = patientRepository.save(patient);
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

}
