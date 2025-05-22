package com.medacare.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medacare.backend.model.SystemSetting;
import com.medacare.backend.repository.SystemSettingRepository;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    public SystemSetting saveSetting(SystemSetting sysSetting) {
        Optional<SystemSetting> existingSetting = systemSettingRepository.findByName(sysSetting.getName());
        SystemSetting systemSetting = existingSetting.orElse(new SystemSetting());
        systemSetting.setName(sysSetting.getName());
        systemSetting.setValue1(sysSetting.getValue1());
        systemSetting.setValue2(sysSetting.getValue2());
        systemSetting.setValue3(sysSetting.getValue3());
        systemSetting.setValue4(sysSetting.getValue4());

        return systemSettingRepository.save(systemSetting);
    }

    public Optional<SystemSetting> getSetting(String name) {
        return systemSettingRepository.findByName(name);
    }
}
