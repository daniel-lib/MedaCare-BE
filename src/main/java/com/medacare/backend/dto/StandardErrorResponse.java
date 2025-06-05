package com.medacare.backend.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StandardErrorResponse {
    private String status;
    private String message;
    private Map<String, String> errors;
}