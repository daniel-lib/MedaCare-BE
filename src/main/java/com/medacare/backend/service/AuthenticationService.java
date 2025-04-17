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

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, RoleRepository roleRepo,
            ResponseService responseService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.responseService = responseService;
        this.emailService = emailService;
    }

    public ResponseEntity<StandardResponse> signup(RegisterUserDto inputData, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse(
                    "error",
                    null,
                    "Invalid Input",
                    result.getAllErrors().toString(), HttpStatus.BAD_REQUEST));
            // return new ResponseEntity<>("Invalid Input" + result.getAllErrors(),
            // HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(inputData.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse(
                            "error",
                            null,
                            "User already exists",
                            null, HttpStatus.BAD_REQUEST));
        }

        RoleEnum roleEnum = null;
        if (inputData.getOrigin().equals(UserOrigin.SELF_REGISTERED.name())
                || inputData.getOrigin() == null) {
            roleEnum = RoleEnum.PATIENT;
        } else {
            roleEnum = RoleEnum.valueOf(inputData.getRole());
        }
        Optional<Role> role = roleRepo.findByName(roleEnum);
        if (role.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", inputData, "Role Not Found", null, HttpStatus.BAD_REQUEST));
        }
        User user = new User();
        user.setFirstName(inputData.getFirstName());
        user.setLastName(inputData.getLastName());
        user.setEmail(inputData.getEmail());
        user.setOrigin(User.UserOrigin.valueOf(inputData.getOrigin()));
        user.setPassword(passwordEncoder.encode(inputData.getPassword()));
        user.setRole(role.get());

        ResponseEntity<StandardResponse> emailVerificationResult = sendVerificationEmail(user);
        return emailVerificationResult;
    }

    public ResponseEntity<StandardResponse> sendVerificationEmail(User user) {
        String verificationEmailResult = emailService.sendVerificationEmail(user);
        if (verificationEmailResult.equals("Error sending email")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.createStandardResponse("error", null, "Error while sending email", null, HttpStatus.INTERNAL_SERVER_ERROR));
        } else {
            user.setVerificationCode(verificationEmailResult);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseService.createStandardResponse("success", user,
                            "Verification email sent.", null, HttpStatus.CREATED));
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
}
