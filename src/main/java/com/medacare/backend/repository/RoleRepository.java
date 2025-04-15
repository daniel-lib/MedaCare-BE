package com.medacare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Role;
import com.medacare.backend.model.RoleEnum;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
    boolean existsByName(RoleEnum name);
    List<Role> findAllByNameIn(List<RoleEnum> names);
    List<Role> findAll();

}
