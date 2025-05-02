package com.medacare.backend.config;

import org.springframework.beans.factory.annotation.Value;

public class FixedVars {
    public static final String BASE_API_VERSION = "/api/v1";

    @Value("${cloudinary.api-key}")
    public static String apiKey;
    
    @Value("${cloudinary.secret-key}")
    public static String secretKey;

    @Value("${cloudinary.cloud-name}")
    public static String cloudName;

    public static final String CLOUDINARY_URL="cloudinary:"+apiKey+":"+secretKey+cloudName;
}
