package com.medacare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginUserDto {
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    private String password;
}