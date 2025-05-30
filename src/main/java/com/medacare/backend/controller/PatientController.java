package com.medacare.backend.controller;

import org.springframework.data.annotation.Transient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.User;
import com.medacare.backend.service.AppointmentService;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.PatientService;
import com.medacare.backend.service.ResponseService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.medacare.backend.repository.PatientRepository;

import org.springframework.web.bind.annotation.RequestParam;

import com.medacare.backend.repository.UserRepository;


@RestController
@RequiredArgsConstructor
@RequestMapping(FixedVars.BASE_API_VERSION + "/patients")
@CrossOrigin
public class PatientController {
    private final PatientService patientService;
    private final AuthenticationService authenticationService;
    private final PatientRepository patientRepository;
    private final ResponseService responseService;
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    // @Transient
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping(value = { "", "/" }, consumes = "application/json")
    public ResponseEntity<StandardResponse> registerPatient(@RequestBody Patient patient) {
        return patientService.savePatientDetail(patient);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<StandardResponse> getAllPatients() {
        return ResponseEntity.ok()
                .body(responseService.createStandardResponse("success",
                        patientService.getAllPatients(), "Patients retrieved", null));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/appointments")
    public ResponseEntity<StandardResponse> getOwnAppointment() {
        Patient patient = patientRepository.findByUser(authenticationService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return ResponseEntity.ok()
                .body(responseService.createStandardResponse("success",
                        appointmentService.findPatientAppointment(patient), "Appointments retrieved", null));
    }


    

    

    // TODO: Implement the getPatientDetails method
    // TODO: Implement the updatePatientDetails method /patients/{id}
    // TODO: Implement the deletePatientDetails method /patients/{id}
    // TODO: Implement the getPatientById method GET /patients/{id}

    // TODO: Implement the getAllPatients method GET /patients

}