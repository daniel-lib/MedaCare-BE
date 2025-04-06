package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medacare.backend.model.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer>{

    List<UserGroup> findAll();
}
