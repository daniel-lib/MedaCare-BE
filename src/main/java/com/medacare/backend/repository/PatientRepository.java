package com.medacare.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{

    List<Patient> findAll();
    Patient findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
