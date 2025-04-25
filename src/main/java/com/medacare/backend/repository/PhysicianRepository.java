package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;

public interface PhysicianRepository extends JpaRepository<Physician, Long>{

    List<Physician> findAll();

    List<Physician> findByHealthcareProvider(Institution healthcareProvider);
}
