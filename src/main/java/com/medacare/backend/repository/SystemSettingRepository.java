package com.medacare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.medacare.backend.model.SystemSetting;

import java.util.List;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    Optional<SystemSetting> findByName(String name);
    List<SystemSetting> findAll();
}