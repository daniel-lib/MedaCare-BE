package com.medacare.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.PhysicianRepository;

@Service
public class PhysicianService {

    private final PhysicianRepository physicianRepository;

    public PhysicianService(PhysicianRepository physicianRepository) {
        this.physicianRepository = physicianRepository;
    }

    public List<Physician> getAllPhysicians() {
        return physicianRepository.findAll();
    }

    public Physician getPhysicianById(Long id) {
        return physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
    }

    public Physician createPhysician(Physician physician) {
        return physicianRepository.save(physician);
    }

    public Physician updatePhysician(Long id, Physician physician) {
        Physician existingPhysician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));

        existingPhysician.setSpecialization(physician.getSpecialization());
        existingPhysician.setLicenseNumber(physician.getLicenseNumber());
        existingPhysician.setAvailabilitySchedule(physician.getAvailabilitySchedule());
        existingPhysician.setOrgnanizationAffiliated(physician.getOrgnanizationAffiliated());
        existingPhysician.setHealthcareProvider(physician.getHealthcareProvider());
        
        return physicianRepository.save(existingPhysician);
    }

    public void deletePhysician(Long id) {
        Physician existingPhysician = physicianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Physician not found with id: " + id));
        physicianRepository.delete(existingPhysician);
    }
}
