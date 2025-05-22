package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Payment;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    List<Payment> findAll(); 
}
