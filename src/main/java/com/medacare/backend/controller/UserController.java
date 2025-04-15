package com.medacare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.model.User;
import com.medacare.backend.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(ApiPaths.BASE_API_VERSION+"/users")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService){
        this.userService = userService;
    }
  
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    public ResponseEntity<User> currentAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @GetMapping()
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}
