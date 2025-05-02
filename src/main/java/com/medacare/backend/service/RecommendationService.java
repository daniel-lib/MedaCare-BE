package com.medacare.backend.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;

@Service
public class RecommendationService {
    private final InstitutionRepository institutionRepository;
    private final PatientService patientService;
    private final PhysicianRepository physicianRepository;

    public RecommendationService(InstitutionRepository institutionRepository, PatientService patientService,
            PhysicianRepository physicianRepository) {
        this.institutionRepository = institutionRepository;
        this.patientService = patientService;
        this.physicianRepository = physicianRepository;
    }

    public List<Institution> recommendInstitution() {
        List<Institution> institutionBySpecialization = institutionRepository
                .findByOfferedSpecializationsIn(
                        patientService.getCurrentUserPatientProfile().getSpecializationPreference());
        List<Institution> allInstitutions = getInstitutionsByRating();
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

    public List<Physician> recommendPhysician() {
        List<Physician> physicianBySpecialization = physicianRepository
                .findBySpecializationIn(patientService.getCurrentUserPatientProfile().getSpecializationPreference());
        List<Physician> allPhysicians = getPhysicianByRating();
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

    public List<String> recommendSpecialization() {
        return physicianRepository.findSpecializationOrderedByRatingDesc();
    }

    public List<Physician> getPhysicianByRating() {
        return physicianRepository.findAllByOrderByRatingDesc();
    }

    public List<Institution> getInstitutionsByRating() {
        return institutionRepository.findAllByOrderByRatingDesc();
    }
}
