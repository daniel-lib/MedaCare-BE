package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;

import io.swagger.v3.oas.annotations.Hidden;

import com.medacare.backend.model.User;

@Hidden
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    List<Institution> findAll();

    List<Institution> findByRequestStatus(InstitutionRegistrationRequestStatus status);

    List<Institution> findByOfferedSpecializationsIn(List<String> specialization);

    List<Institution> findAllByOrderByRatingDesc();

    Institution findByAdminUser(User adminUser);

    @Query("SELECT i FROM Institution i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.offeredSpecializations) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Institution> searchByKeyword(String keyword);

}
