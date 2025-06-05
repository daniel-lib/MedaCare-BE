package com.medacare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Physician.AccountRequestStatus;

import io.swagger.v3.oas.annotations.Hidden;

import com.medacare.backend.model.User;

@Hidden
@Repository
public interface PhysicianRepository extends JpaRepository<Physician, Long> {

    List<Physician> findAll();

    List<Physician> findByAccountRequestStatusNot(AccountRequestStatus status);

    Physician findByUserId(long userId);

    boolean existsByUserId(long userId);

    Optional<Physician> findByUser(User user);

    boolean existsByUser(User user);


    List<Physician> findAllByOrderByRatingDesc();

    List<Physician> findByHealthcareProvider(Institution healthcareProvider);

    List<Physician> findBySpecializationIn(List<String> specialization);

    List<Physician> findByAccountRequestStatus(AccountRequestStatus status);

    @Query(value = "SELECT DISTINCT specInner.specialization \n" + //
            "FROM(SELECT specialization\n" + //
            "FROM physician ORDER BY rating DESC) specInner", nativeQuery = true)
    List<String> findSpecializationOrderedByRatingDesc();

    List<Physician> findBySpecialization(String specialization);

    @Query(value = """
    SELECT * FROM physician p
    LEFT JOIN user u ON p.user_id = u.id
    WHERE LOWER(p.specialization) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(p.phone_number) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """, nativeQuery = true)
List<Physician> searchByKeyword(String keyword);

}
