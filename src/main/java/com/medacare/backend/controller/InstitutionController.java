package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.service.InstitutionService;
import com.medacare.backend.service.ResponseService;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.BASE_API_VERSION + "/institutions")
@CrossOrigin
public class InstitutionController {

    private final InstitutionService institutionService;
    private final ResponseService responseService;

    public InstitutionController(InstitutionService institutionService, ResponseService responseService) {
        this.institutionService = institutionService;
        this.responseService = responseService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<StandardResponse> getAllInstitutions() {
        List<Institution> institutions = institutionService.getAllInstitutions();
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", institutions,
                        "Institutions retrieved successfully", null, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getInstitutionById(@PathVariable Long id) {
        Institution institution = institutionService.getInstitutionById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", institution,
                        "Institution retrieved successfully", null, HttpStatus.OK));
    }

    @PreAuthorize("permitAll()")
    @PostMapping
    public ResponseEntity<StandardResponse> submitInstitutionRegistrationRequest(@RequestBody Institution institution) {
        Institution createdInstitution = institutionService.createInstitution(institution);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.createStandardResponse("success", createdInstitution,
                        "Institution registration request submitted successfully", null, HttpStatus.CREATED));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateInstitution(@PathVariable Long id,
            @RequestBody Institution institution) {
        Institution updatedInstitution = institutionService.updateInstitution(id, institution);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", updatedInstitution,
                        "Institution updated successfully", null, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(responseService.createStandardResponse("success", null,
                        "Institution deleted successfully", null, HttpStatus.NO_CONTENT));
    }
}
