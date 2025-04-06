package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>{

    List<MedicalRecord> findAll();
}
