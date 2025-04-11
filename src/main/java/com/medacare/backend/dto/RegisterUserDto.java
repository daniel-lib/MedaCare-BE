package com.medacare.backend.dto;

import com.medacare.backend.model.User.UserOrigin;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {
    @Column(unique = true)
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password is required")
    private String password;

    @Size(min = 3, max = 100, message = "First name must be between 3 and 100 characters")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Size(min = 3, max = 100, message = "Last name must be between 3 and 100 characters")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "User Registration Origin is required")
    // @Enumerated(EnumType.STRING)
    private String origin;

}
