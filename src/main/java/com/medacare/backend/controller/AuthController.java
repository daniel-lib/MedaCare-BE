package com.medacare.backend.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.LoginUserDto;
import com.medacare.backend.dto.PasswordResetDto;
import com.medacare.backend.dto.PasswordUpdateDto;
import com.medacare.backend.dto.RegisterUserDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.security.LoginResponse;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.JwtService;
import com.medacare.backend.service.ResponseService;
import com.medacare.backend.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
@RequestMapping(FixedVars.BASE_API_VERSION + "/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepo;
    private final AuthenticationService authenticationService;
    private final ResponseService responseService;

    public AuthController(UserRepository userRepo, AuthenticationService authenticationService,
            ResponseService responseService, UserService userService) {
        this.userRepo = userRepo;
        this.authenticationService = authenticationService;
        this.responseService = responseService;
        this.userService = userService;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<StandardResponse> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        ResponseEntity<StandardResponse> registerationResult = authenticationService.signup(registerUserDto);
        return registerationResult;
    }

    @PostMapping("/verify-email")
    public ResponseEntity<StandardResponse> verifyEmailAddress(@RequestParam("email") String email,
            @RequestParam("token") String token) {
        Optional<User> user = userRepo.findByEmail(email);
        if (!user.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse("error",
                    email, "There is no registered user with that email", null));
        if (user.get().isVerified()) {
            LoginResponse loginResponse = authenticationService.generateLoginResponse(user.get());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", loginResponse, "User is already verified",
                            null));
        }

        if (user.get().getVerificationCode() == null || !user.get().getVerificationCode().equals(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", email, "Invalid verification code", null));
        user.get().setVerified(true);
        user.get().setVerificationCode(null);
        userRepo.save(user.get());
        LoginResponse loginResponse = authenticationService.generateLoginResponse(user.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", loginResponse, "User verified", null));
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {

        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        if (!authenticatedUser.isVerified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(responseService.createStandardResponse("error", null, "User is not verified", null));
        } else {
            LoginResponse loginResponse = authenticationService.generateLoginResponse(authenticatedUser);
            return ResponseEntity
                    .ok(responseService.createStandardResponse("success", loginResponse, "User authenticated", null));
        }
    }

    @PostMapping("/email/verification")
    public ResponseEntity<StandardResponse> sendEmailVerification(@RequestParam("email") String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "User not found", null));
        }
        User user = optionalUser.get();
        if (user.isVerified()) {
            LoginResponse loginResponse = authenticationService.generateLoginResponse(user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", loginResponse, "User is already verified",
                            null));
        }
        return authenticationService.sendVerificationEmail(user);
    }

    @PostMapping("/verify-verification-code")
    public ResponseEntity<StandardResponse> verifyVerificationCode(@RequestParam("email") String email,
            @RequestParam("code") String verificationCode) {
        Optional<User> user = userRepo.findByEmail(email);
        if (!user.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse("error",
                    email, "There is no registered user with that email", null));

        if (user.get().getVerificationCode() == null || !user.get().getVerificationCode().equals(verificationCode))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "Invalid verification code", null));
        user.get().setVerificationCode(null);
        userRepo.save(user.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", null, "Account ownership verified",
                        null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<StandardResponse> resetPasswordRequest(@RequestParam("email") String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "User not found", null));
        }
        User user = optionalUser.get();
        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "User is not verified", null));
        }
        return authenticationService.sendPasswordResetEmail(user);
    }

    @PatchMapping("/password-reset")
    public ResponseEntity<StandardResponse> resetPasswordRequest(@Valid @RequestBody PasswordResetDto passwordResetData) {
        Optional<User> optionalUser = userRepo.findByEmail(passwordResetData.getEmail());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "Email address is not found", null));
        }
        User user = optionalUser.get();
        user.setPassword(new BCryptPasswordEncoder().encode(passwordResetData.getPassword()));
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", null, "Password reset successfully", null));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    public ResponseEntity<StandardResponse> updatePassword(@Valid @RequestBody PasswordUpdateDto passwordUpdateData) {
        Optional<User> optionalUser = userRepo.findByEmail(passwordUpdateData.getEmail());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "User not found", null));
        }
        User user = optionalUser.get();
        if (!new BCryptPasswordEncoder().matches(passwordUpdateData.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "Old password is incorrect", null));
        }
        user.setPassword(new BCryptPasswordEncoder().encode(passwordUpdateData.getNewPassword()));
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", null, "Password updated successfully", null));
    }
}
