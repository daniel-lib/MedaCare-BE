package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.PhysicianService;
import com.medacare.backend.service.ResponseService;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.BASE_API_VERSION + "/physicians")
public class PhysicianController {

        private final PhysicianRepository physicianRepository;

        private final PhysicianService physicianService;
        private final ResponseService responseService;
        private final AuthenticationService authenticationService;

        public PhysicianController(PhysicianService physicianService, ResponseService responseService,
                        AuthenticationService authenticationService,
                        PhysicianRepository physicianRepository) {
                this.physicianService = physicianService;
                this.responseService = responseService;
                this.authenticationService = authenticationService;
                this.physicianRepository = physicianRepository;
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping
        public ResponseEntity<StandardResponse> getAllPhysicians() {
                List<Physician> physicians = physicianService.getAllPhysicians();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", physicians,
                                                "Physicians retrieved successfully",
                                                null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/{id}")
        public ResponseEntity<StandardResponse> getPhysicianById(@PathVariable Long id) {
                Physician physician = physicianService.getPhysicianById(id);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", physician,
                                                "Physician retrieved successfully",
                                                null));
        }

        @PreAuthorize("hasRole('PHYSICIAN')")
        @PostMapping
        public ResponseEntity<StandardResponse> createPhysician(@RequestBody Physician physician) {
                Long userId = authenticationService.getCurrentUser().getId();
                if (physicianRepository.existsById(userId)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", physician,
                                                        "Physician info already saved", null));
                }
                physician.setUser(authenticationService.getCurrentUser());
                Physician createdPhysician = physicianService.createPhysician(physician);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(responseService.createStandardResponse("success", createdPhysician,
                                                "Physician created successfully", null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<StandardResponse> updatePhysician(@PathVariable Long id,
                        @RequestBody Physician physician) {
                Physician updatedPhysician = physicianService.updatePhysician(id, physician);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", updatedPhysician,
                                                "Physician updated successfully", null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<StandardResponse> deletePhysician(@PathVariable Long id) {
                physicianService.deletePhysician(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .body(responseService.createStandardResponse("success", null,
                                                "Physician deleted successfully", null));
        }
}