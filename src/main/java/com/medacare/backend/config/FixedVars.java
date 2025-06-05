package com.medacare.backend.config;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public class FixedVars {
    public static final String BASE_API_VERSION = "/api/v1";
    public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    public static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList("application/pdf");
    public static final OffsetDateTime DEFAULT_ZONED_DATE_TIME = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0);

    @Value("${cloudinary.api-key}")
    public static String apiKey;

    @Value("${cloudinary.secret-key}")
    public static String secretKey;

    @Value("${cloudinary.cloud-name}")
    public static String cloudName;

    public static final String CLOUDINARY_URL = "cloudinary:" + apiKey + ":" + secretKey + cloudName;

}
