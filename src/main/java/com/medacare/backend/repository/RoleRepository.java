package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

    List<Role> findAll();
}
