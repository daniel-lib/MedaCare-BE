package com.medacare.backend.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.dto.LoginUserDto;
import com.medacare.backend.dto.RegisterUserDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.security.LoginResponse;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.EmailService;
import com.medacare.backend.service.JwtService;
import com.medacare.backend.service.ResponseService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(ApiPaths.BASE_API_VERSION + "/auth")
@RestController
// @CrossOrigin(origins = { "http://localhost:5173", "https://medacare-fe.onrender.com" })
@CrossOrigin
public class AuthController {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final ResponseService responseService;

    public AuthController(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService, AuthenticationService authenticationService,
            ResponseService responseService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.responseService = responseService;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<StandardResponse> register(@Valid @RequestBody RegisterUserDto registerUserDto,
            BindingResult result) {
        ResponseEntity<StandardResponse> registerationResult = authenticationService.signup(registerUserDto, result);
        return registerationResult;
    }

    @PostMapping("/verify-email")
    public ResponseEntity<StandardResponse> getMethodName(@RequestParam("email") String email,
            @RequestParam("token") String token) {
        Optional<User> user = userRepo.findByEmail(email);
        if (!user.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse("error",
                    email, "There is no registered user with that email", null, HttpStatus.BAD_REQUEST));
        if (user.get().isVerified())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", email, "User is already verified", null, HttpStatus.BAD_REQUEST));

        if (user.get().getVerificationCode() == null || !user.get().getVerificationCode().equals(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", email, "Invalid verification code", null, HttpStatus.BAD_REQUEST));
        user.get().setVerified(true);
        user.get().setVerificationCode(null);
        userRepo.save(user.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", email, "User verified", null, HttpStatus.OK));
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {

        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        if (!authenticatedUser.isVerified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(responseService.createStandardResponse("error", null, "User is not verified", null, HttpStatus.UNAUTHORIZED));
        } else {
            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            return ResponseEntity
                    .ok(responseService.createStandardResponse("success", loginResponse, "User authenticated", null, HttpStatus.OK));
        }
    }

    @PostMapping("/email/verification")
    public ResponseEntity<StandardResponse> sendEmailVerification(@RequestParam("email") String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "User not found", null, HttpStatus.BAD_REQUEST));
        }
        User user = optionalUser.get();
        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null, "User is already verified", null, HttpStatus.BAD_REQUEST));
        }
        return authenticationService.sendVerificationEmail(user);
    }

}
