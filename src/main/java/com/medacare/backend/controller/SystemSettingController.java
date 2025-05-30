package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.SystemSetting;
import com.medacare.backend.service.ResponseService;
import com.medacare.backend.service.SystemSettingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/system-settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;
    private final ResponseService responseService;

    @PostMapping
    public ResponseEntity<SystemSetting> saveSetting(@RequestParam String name,
            @RequestBody SystemSetting systemSetting) {
        SystemSetting savedSetting = systemSettingService.saveSetting(systemSetting);
        return ResponseEntity.ok(savedSetting);
    }

    @GetMapping("/{name}")
    public ResponseEntity<StandardResponse> getSetting(@PathVariable String name) {
        SystemSetting sysSetting = systemSettingService.getSetting(name)
                .orElseThrow(() -> new RuntimeException("Setting not found"));

        return ResponseEntity.ok().body(responseService.createStandardResponse("success", sysSetting, "System setting fetched", null));
    }
}