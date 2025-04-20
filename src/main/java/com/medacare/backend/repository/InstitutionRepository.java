package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.Institution;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    List<Institution> findAll();

}
