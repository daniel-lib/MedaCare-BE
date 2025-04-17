package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.ApiPaths;

@RequestMapping(ApiPaths.BASE_API_VERSION+"/")
@RestController
public class GeneralController {
    
    @GetMapping(value = "hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Welcome to MedaCare API!");
    }
    


}
