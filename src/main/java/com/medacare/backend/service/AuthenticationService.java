package com.medacare.backend.service;

import com.medacare.backend.dto.LoginUserDto;
import com.medacare.backend.dto.RegisterUserDto;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.UserRepository;

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

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<String> signup(RegisterUserDto input, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("Invalid Input" + result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(input.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setOrigin(User.UserOrigin.valueOf(input.getOrigin()));
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("User has been created", HttpStatus.CREATED);

    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
