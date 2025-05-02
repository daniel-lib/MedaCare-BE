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
import com.medacare.backend.service.PatientService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/patients")
@CrossOrigin
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // @Transient
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping(value = { "", "/" }, consumes = "application/json")
    public ResponseEntity<StandardResponse> registerPatient(@RequestBody Patient patient) {
        return patientService.savePatientDetail(patient);
    }

    // TODO: Implement the getPatientDetails method
    // TODO: Implement the updatePatientDetails method /patients/{id}
    // TODO: Implement the deletePatientDetails method /patients/{id}
    // TODO: Implement the getPatientById method GET /patients/{id}

    // TODO: Implement the getAllPatients method GET /patients

}