package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Patient;
import com.medacare.backend.repository.PatientRepository;
@RestController
@CrossOrigin
@RequestMapping(ApiPaths.BASE_API_VERSION + "/patients")
public class PatientController {
    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // @GetMapping("{id}")
    // public ResponseEntity<StandardResponse> getPatientDetails() {
    //     Patient patient = patientRepository.findById(1L).orElse(null); // Example ID, replace with actual logic
    //     if (patient == null) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     // StandardResponse response = new StandardResponse("Success", "Patient details retrieved successfully", null);
    //     return ResponseEntity.ok(response);
    // }

}