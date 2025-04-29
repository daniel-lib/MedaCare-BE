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

import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.service.AI_AssistanceService;
import com.medacare.backend.service.PatientService;

@RestController
@RequestMapping(ApiPaths.BASE_API_VERSION + "/recommendations")
public class RecommendationController {

    private final AI_AssistanceService assistanceService;
    private final InstitutionRepository institutionRepository;
    private final PhysicianRepository physicianRepository;
    private final PatientService patientService;

    public RecommendationController(AI_AssistanceService assistanceService,
            InstitutionRepository institutionRepository, PhysicianRepository physicianRepository,
            PatientService patientService) {
        this.assistanceService = assistanceService;
        this.institutionRepository = institutionRepository;
        this.physicianRepository = physicianRepository;
        this.patientService = patientService;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/specialty")
    public List<String> recommendSpecialization() {

        return new ArrayList<>();
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/institution")
    public List<Institution> recommendInstitution() {

        List<Institution> institutionBySpecialization = institutionRepository
                .findByOfferedSpecializationsIn(patientService.getCurrentUserPatientProfile().getSpecializationPreference());
        List<Institution> allInstitutions = institutionRepository.findAllByOrderByRatingDesc();
        Random random = new Random();
        Set<Institution> recommendedInstitutions = new HashSet<>();

        recommendedInstitutions.addAll(institutionBySpecialization);
        
        while (recommendedInstitutions.size() < 8 && recommendedInstitutions.size() < allInstitutions.size()) {
            int randomIndex = random.nextInt(0, allInstitutions.size());
            Institution randomInstitution = allInstitutions.get(randomIndex);
            if (!recommendedInstitutions.contains(randomInstitution)) {
                recommendedInstitutions.add(randomInstitution);
            }
        }
        List<Institution> recommendedInstitutionsList = new ArrayList<>();
        recommendedInstitutionsList.addAll(recommendedInstitutions);
        return recommendedInstitutionsList;
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/physician")
    public List<Physician> recommendPhysician() {
        List<Physician> physicianBySpecialization = physicianRepository
                .findBySpecializationIn(patientService.getCurrentUserPatientProfile().getSpecializationPreference());
        List<Physician> allPhysicians = physicianRepository.findAllByOrderByRatingDesc();
        Random random = new Random();
        Set<Physician> recommendedPhysicians = new HashSet<>();

        recommendedPhysicians.addAll(physicianBySpecialization);
        
        while (recommendedPhysicians.size() < 8 && recommendedPhysicians.size() < allPhysicians.size()) {
            int randomIndex = random.nextInt(0, allPhysicians.size());
            Physician randomPhysician = allPhysicians.get(randomIndex);
            if (!recommendedPhysicians.contains(randomPhysician)) {
                recommendedPhysicians.add(randomPhysician);
            }
        }
        List<Physician> recommendedPhysiciansList = new ArrayList<>();
        recommendedPhysiciansList.addAll(recommendedPhysicians);
        return recommendedPhysiciansList;
    }
}
