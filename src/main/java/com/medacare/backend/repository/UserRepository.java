package com.medacare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.User;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrId(String email, Long Id);
    boolean existsByEmailIgnoreCase(String email);

}
