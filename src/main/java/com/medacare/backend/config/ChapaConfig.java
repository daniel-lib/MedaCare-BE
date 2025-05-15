package com.medacare.backend.config;

import java.math.BigDecimal;

import org.apache.catalina.filters.SetCharacterEncodingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.medacare.backend.model.User;
import com.yaphet.chapa.Chapa;
import com.yaphet.chapa.model.Customization;
import com.yaphet.chapa.model.InitializeResponseData;
import com.yaphet.chapa.model.PostData;
import com.yaphet.chapa.utility.Util;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChapaConfig {
    @Value("${CHAPA_SECRET_KEY}")
    private String SECRET_KEY;

    @Bean
    Chapa configureChapa() {
        Chapa chapa = new Chapa(SECRET_KEY);
        return chapa;
    }

    
}
