package com.medacare.backend.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardErrorResponse {
    private String status;
    private String message;
    private Map<String, String> errors;
}