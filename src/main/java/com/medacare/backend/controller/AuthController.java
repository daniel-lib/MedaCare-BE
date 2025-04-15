package com.medacare.backend.controller;

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
import com.medacare.backend.model.User;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.security.LoginResponse;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.JwtService;

import jakarta.validation.Valid;

@RequestMapping(ApiPaths.BASE_API_VERSION+"/auth")
@RestController
public class AuthController {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;    
    private final AuthenticationService authenticationService;
    public AuthController(UserRepository userRepo, BCryptPasswordEncoder passwordEncoder,
    JwtService jwtService, AuthenticationService authenticationService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }
    


    @PostMapping(value = "/signup", consumes="application/json")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserDto registerUserDto, BindingResult result) {
        ResponseEntity<String> registerationResult = authenticationService.signup(registerUserDto, result);
        return registerationResult;
    }
    

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

}
