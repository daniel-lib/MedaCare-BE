package com.medacare.backend.service;

import com.medacare.backend.dto.LoginUserDto;
import com.medacare.backend.dto.RegisterUserDto;
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

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, RoleRepository roleRepo) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }

    public ResponseEntity<String> signup(RegisterUserDto inputData, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("Invalid Input" + result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(inputData.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }

        RoleEnum roleEnum = null;
        if(inputData.getOrigin().equals(UserOrigin.SELF_REGISTERED.name())) {
            roleEnum = RoleEnum.PATIENT;
        } else {
            roleEnum = RoleEnum.valueOf(inputData.getRole());
        }
        Optional<Role> role = roleRepo.findByName(roleEnum);
        if (role.isEmpty()) {
            return new ResponseEntity<>("Role not found", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setFirstName(inputData.getFirstName());
        user.setLastName(inputData.getLastName());
        user.setEmail(inputData.getEmail());
        user.setOrigin(User.UserOrigin.valueOf(inputData.getOrigin()));
        user.setPassword(passwordEncoder.encode(inputData.getPassword()));
        user.setRole(role.get());
        userRepository.save(user);

        return new ResponseEntity<>("User has been created", HttpStatus.CREATED);

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
