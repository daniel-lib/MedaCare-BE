package com.medacare.backend.service;

import com.medacare.backend.dto.LoginUserDto;
import com.medacare.backend.dto.RegisterUserDto;
import com.medacare.backend.model.Role;
import com.medacare.backend.model.User;
import com.medacare.backend.model.User.UserOrigin;
import com.medacare.backend.repository.UserRepository;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public User createUser(String email, String firstName, String lastName, Role role, UserOrigin origin,
            boolean verified, boolean firstLogin) {
        User user = new User();
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setVerified(verified);
        user.setFirstLogin(firstLogin);

        user.setOrigin(origin);
        // user = userRepo.save(user);
        return userRepo.save(user);
    }
}
