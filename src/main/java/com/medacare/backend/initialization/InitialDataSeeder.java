package com.medacare.backend.initialization;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.medacare.backend.model.SystemSetting;
import com.medacare.backend.service.SystemSettingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialDataSeeder implements CommandLineRunner {

        private final SystemSettingService systemSettingService;

        @Override
        public void run(String... args) throws Exception {
                this.seedDefaultSetting();
        }

        private void seedDefaultSetting() {
                SystemSetting standardPriceSetting = new SystemSetting("StandardSerivcePrice", String.valueOf(500),
                                null, null, null);
                SystemSetting standardFeeCurrencySetting = new SystemSetting("StandardSerivceFeeCurrency", "ETB", null, null,
                                null);
                systemSettingService.saveSetting(standardPriceSetting);
                systemSettingService.saveSetting(standardFeeCurrencySetting);

        }

}