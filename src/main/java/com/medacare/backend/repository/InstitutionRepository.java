package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    List<Institution> findAll();
    List<Institution> findByRequestStatus(InstitutionRegistrationRequestStatus status);

}
