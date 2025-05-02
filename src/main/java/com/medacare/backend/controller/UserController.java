package com.medacare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.model.User;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(FixedVars.BASE_API_VERSION+"/users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }
  
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    public ResponseEntity<User> currentAuthenticatedUser(){        
        User currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @GetMapping()
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}
