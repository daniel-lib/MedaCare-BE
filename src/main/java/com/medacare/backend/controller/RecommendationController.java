package com.medacare.backend.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.service.AI_AssistanceService;
import com.medacare.backend.service.PatientService;
import com.medacare.backend.service.RecommendationService;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/recommendations")
public class RecommendationController {

    private final AI_AssistanceService assistanceService;
    private final InstitutionRepository institutionRepository;
    private final PhysicianRepository physicianRepository;
    private final PatientService patientService;
    private final RecommendationService recommendationService;

    public RecommendationController(AI_AssistanceService assistanceService,
            InstitutionRepository institutionRepository, PhysicianRepository physicianRepository,
            PatientService patientService, RecommendationService recommendationService) {
        this.assistanceService = assistanceService;
        this.institutionRepository = institutionRepository;
        this.physicianRepository = physicianRepository;
        this.patientService = patientService;
        this.recommendationService = recommendationService;
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/specialty")
    public List<String> recommendSpecialization() {
        return recommendationService.recommendSpecialization();
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/institution")
    public List<Institution> recommendInstitution() {
        return recommendationService.recommendInstitution();
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/physician")
    public List<Physician> recommendPhysician() {
        return recommendationService.recommendPhysician();
    }
}
