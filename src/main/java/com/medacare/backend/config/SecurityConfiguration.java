package com.medacare.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomCorsConfiguration customCorsConfiguration;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            CustomCorsConfiguration customCorsConfiguration) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customCorsConfiguration = customCorsConfiguration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                        // "/swagger-ui.html").permitAll()
                        .requestMatchers(FixedVars.BASE_API_VERSION + "/auth/**", "/api/hello", "/api-docs/**",
                                "/swagger-ui/**","/",
                                "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-ui/index.html/", "/swagger-ui.html","/api/v1/example/data",
                                FixedVars.BASE_API_VERSION+"/institutions", FixedVars.BASE_API_VERSION+"/institutions/", FixedVars.BASE_API_VERSION+"/assistance/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                        .cors(cors -> cors.configurationSource(customCorsConfiguration))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}