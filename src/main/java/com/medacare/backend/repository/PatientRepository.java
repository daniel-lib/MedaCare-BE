package com.medacare.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long>{

    List<Patient> findAll();
}
