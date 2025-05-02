package com.medacare.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Interaction;
import com.medacare.backend.service.InteractionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/interactions")
public class InteractionController {

    public final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/rating")
    public ResponseEntity<StandardResponse> rate(@Valid @RequestBody Interaction interaction) {
        // Logic to handle rating
        return interactionService.rate(interaction);
    }

}
