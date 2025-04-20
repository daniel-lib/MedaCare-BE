package com.medacare.backend.dto;

import org.springframework.lang.Nullable;

import com.medacare.backend.model.User.UserOrigin;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {
    @Column(unique = true)
    @NotBlank(message = "Email is required")
    @Pattern(
    regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
    message = "Invalid email format")
    @Size(min = 4, max = 100, message = "Email must be between 4 and 100 characters")
    @Enumerated(EnumType.STRING)
    private String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character"
    )
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

    // @Nullable
    private String role;

}
