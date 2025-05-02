package com.medacare.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.service.AI_AssistanceService;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.PatientService;
import com.medacare.backend.model.Patient;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/assistance")
public class AssistanceController {

    private final AI_AssistanceService assistanceService;
    private final AuthenticationService authenticationService;
    private final PatientService patientService;

    public AssistanceController(AI_AssistanceService assistanceService,
            AuthenticationService authenticationService, PatientService patientService) {
        this.assistanceService = assistanceService;
        this.authenticationService = authenticationService;
        this.patientService = patientService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/advice")
    public String adviseForSearch(@RequestBody(required = false) String symptoms) {
        Patient currentPatient = patientService.getCurrentUserPatientProfile();
        return assistanceService.adviseForSearch(symptoms, currentPatient);
    }
}
