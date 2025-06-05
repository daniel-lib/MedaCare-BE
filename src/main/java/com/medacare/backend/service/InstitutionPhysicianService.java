package com.medacare.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstitutionPhysicianService {
    private final InstitutionRepository institutionRepository;
    private final PhysicianRepository physicianRepository;

    public List<Physician> getInstitutionPhysiciansById(Long institutionId) {
        List<Physician> institutionPhysician = this.getPhysiciansByHealthcareProvider(institutionId);
        return institutionPhysician;
    }

    public List<Physician> getPhysiciansByHealthcareProvider(Long healthcareProviderId) {
        Institution institution = institutionRepository.findById(healthcareProviderId)
                .orElseThrow(
                        () -> new RuntimeException("Healthcare provider not found with id: " + healthcareProviderId));
        return physicianRepository.findByHealthcareProvider(institution);
    }
}
