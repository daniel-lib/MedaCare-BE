package com.medacare.backend.service;

import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.repository.InstitutionRepository;

import jakarta.validation.Valid;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Institution getInstitutionById(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
    }

    public Institution createInstitution(@Valid Institution institution) {
        institution.setRequestStatus(InstitutionRegistrationRequestStatus.PENDING);
        return institutionRepository.save(institution);
    }

    public Institution updateInstitution(Long id, Institution institution) {
        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));

        existingInstitution.setName(institution.getName());
        existingInstitution.setType(institution.getType());
        existingInstitution.setCountry(institution.getCountry());
        existingInstitution.setRegionOrState(institution.getRegionOrState());
        existingInstitution.setSubCityOrDistrict(institution.getSubCityOrDistrict());
        existingInstitution.setStreet(institution.getStreet());
        existingInstitution.setRegistrationLicenseNumber(institution.getRegistrationLicenseNumber());
        existingInstitution.setYearEstablished(institution.getYearEstablished());
        existingInstitution.setAboutInstitution(institution.getAboutInstitution());
        existingInstitution.setEmail(institution.getEmail());
        existingInstitution.setPrimaryContactPersonName(institution.getPrimaryContactPersonName());
        existingInstitution.setPrimaryContactPersonRole(institution.getPrimaryContactPersonRole());
        existingInstitution.setOfferedServices(institution.getOfferedServices());
        existingInstitution.setAvailableFacilities(institution.getAvailableFacilities());
        existingInstitution.setOfferedSpecializations(institution.getOfferedSpecializations());

        return institutionRepository.save(existingInstitution);
    }

    public void deleteInstitution(Long id) {
        Institution existingInstitution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found with id: " + id));
        institutionRepository.delete(existingInstitution);
    }
}
