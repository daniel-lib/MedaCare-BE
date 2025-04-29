package com.medacare.backend.repository;

import java.util.List;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;

@Repository
public interface PhysicianRepository extends JpaRepository<Physician, Long>{

    List<Physician> findAll();

    // Page <Physician> findAllOrderByRatingDesc(Pageable p);
    
    List<Physician> findAllByOrderByRatingDesc();

    List<Physician> findByHealthcareProvider(Institution healthcareProvider);
    List<Physician> findBySpecializationIn(List<String> specialization);
}
