package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

}
