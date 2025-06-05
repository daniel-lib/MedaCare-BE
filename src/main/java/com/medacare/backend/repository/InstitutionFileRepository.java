package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.helper.InstitutionFile;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface  InstitutionFileRepository extends  JpaRepository<InstitutionFile, Long>{
    List<InstitutionFile> findAll();
}
