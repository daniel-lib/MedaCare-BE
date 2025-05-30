package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.service.MedicalRecordService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.medacare.backend.model.MedicalRecord;


@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT')")
    @PostMapping("/{appointmentId}")
    public ResponseEntity<StandardResponse> addToMedicalRecord(@RequestBody MedicalRecord medicalRecord,
            @PathVariable Long appointmentId) {
        return medicalRecordService.addToMedicalRecord(medicalRecord, appointmentId);

    }

    @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<StandardResponse> getMedicalRecordsByPatientId(@PathVariable Long patientId) {
        return medicalRecordService.getMedicalRecordsByPatientId(patientId);
    }

    @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT', 'ORG_ADMIN')")
    @GetMapping("/institution/patient/{patientId}")
    public ResponseEntity<StandardResponse> getMedicalRecordsForInstitutionPatient(@PathVariable Long patientId) {
        return medicalRecordService.getMedicalRecordsByPatientId(patientId);
    }


}
