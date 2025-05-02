package com.medacare.backend.repository;

import java.util.List;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Physician.AccountRequestStatus;

@Repository
public interface PhysicianRepository extends JpaRepository<Physician, Long> {

    List<Physician> findAll();

    List<Physician> findByAccountRequestStatusNot(AccountRequestStatus status);



    // Page <Physician> findAllOrderByRatingDesc(Pageable p);

    List<Physician> findAllByOrderByRatingDesc();

    List<Physician> findByHealthcareProvider(Institution healthcareProvider);

    List<Physician> findBySpecializationIn(List<String> specialization);
    List<Physician> findByAccountRequestStatus(AccountRequestStatus status);

    @Query(value = "SELECT DISTINCT specInner.specialization \n" + //
            "FROM(SELECT specialization\n" + //
            "FROM physician ORDER BY rating DESC) specInner", nativeQuery = true)
    List<String> findSpecializationOrderedByRatingDesc();

    List<Physician> findBySpecialization(String specialization);
}
