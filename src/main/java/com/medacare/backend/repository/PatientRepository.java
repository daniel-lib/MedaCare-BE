package com.medacare.backend.repository;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.medacare.backend.dto.report.PatientGenderCountDto;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.User;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{

    List<Patient> findAll();
    Patient findByUserId(Long userId);
    Optional<Patient> findByUser(User user);
    boolean existsByUserId(Long userId);

    @Query(value = "SELECT count(gender) patientCount, gender FROM patient group By gender", nativeQuery = true)
    List<PatientGenderCountDto> findPatientsByGender();
}
