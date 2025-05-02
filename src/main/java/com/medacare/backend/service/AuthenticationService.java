package com.medacare.backend.service;

import com.medacare.backend.dto.LoginUserDto;
import com.medacare.backend.dto.RegisterUserDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.RoleEnum;
import com.medacare.backend.model.User;
import com.medacare.backend.model.User.UserOrigin;
import com.medacare.backend.repository.RoleRepository;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.security.LoginResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepo;
    private final ResponseService responseService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, RoleRepository roleRepo,
            ResponseService responseService, EmailService emailService,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.responseService = responseService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public ResponseEntity<StandardResponse> signup(@Valid @RequestBody RegisterUserDto inputData) {
        if (userRepository.existsByEmail(inputData.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse(
                            "error",
                            null,
                            "User already exists",
                            null));
        }

        RoleEnum roleEnum = null;
        if (inputData.getRole() == null) {
            roleEnum = RoleEnum.PATIENT;
            inputData.setOrigin(UserOrigin.SELF_REGISTERED.name());

        } else {
            try {
                roleEnum = RoleEnum.valueOf(inputData.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(responseService.createStandardResponse("error", null, "Invalid Role",
                                inputData.getRole() + " is not a valid role."));
            }
        }
        if (roleEnum != RoleEnum.PATIENT && roleEnum != RoleEnum.PHYSICIAN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "Invalid Role",
                            "Organization roles are not allowed for self-registration."));
        }
        Optional<Role> role = roleRepo.findByName(roleEnum);
        if (role.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", inputData, "Role Not Found", null));
        }
        User user = new User();
        user.setFirstName(inputData.getFirstName());
        user.setLastName(inputData.getLastName());
        user.setEmail(inputData.getEmail());
        try {
            user.setOrigin(User.UserOrigin.valueOf(inputData.getOrigin()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "Invalid value for origin",
                            inputData.getOrigin() + " is not a valid origin."));
        }
        user.setPassword(passwordEncoder.encode(inputData.getPassword()));
        user.setRole(role.get());

        ResponseEntity<StandardResponse> emailVerificationResult = sendVerificationEmail(user);
        return emailVerificationResult;
    }

    public ResponseEntity<StandardResponse> sendVerificationEmail(User user) {
        String verificationEmailResult = emailService.sendVerificationEmail(user, "MedaCare Email Verification Code",
                "Thank you for signing up. Please verify your email address, by entering the code below.",
                "Welcome to MedaCare");
        if (verificationEmailResult.equals("Error sending email")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.createStandardResponse("error", null,
                            "Could not send email. Make sure you've used valid email.", null));
        } else {
            user.setVerificationCode(verificationEmailResult);
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseService.createStandardResponse("success", savedUser,
                            "Verification email sent.", null));
        }
    }

    public ResponseEntity<StandardResponse> sendPasswordResetEmail(User user) {
        String verificationEmailResult = emailService.sendVerificationEmail(user, "MedaCare Email Verification Code",
                "You requested for password reset. Please verify you own the account by entering the code below.",
                "MedaCare Password Reset");
        if (verificationEmailResult.equals("Error sending email")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.createStandardResponse("error", null,
                            "Could not send email. Make sure you've typed valid email.", null));
        } else {
            user.setVerificationCode(verificationEmailResult);
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseService.createStandardResponse("success", null,
                            "Password reset email sent.", null));
        }
    }

    public User authenticate(LoginUserDto inputData) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        inputData.getEmail(),
                        inputData.getPassword()));

        return userRepository.findByEmail(inputData.getEmail())
                .orElseThrow();
    }

    public LoginResponse generateLoginResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return loginResponse;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
        // return userRepository.findByEmail(jwtService.getCurrentUserEmail())
        // .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
