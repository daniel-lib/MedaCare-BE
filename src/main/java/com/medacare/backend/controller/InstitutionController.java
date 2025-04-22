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

import jakarta.validation.Valid;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending/requests")
    public ResponseEntity<StandardResponse> getAllInstitutions() {
        List<Institution> institutions = institutionService.getAllPendingInstitutions();
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", institutions,
                        "Institutions retrieved successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/requests/{id}/{status}")
    public ResponseEntity<StandardResponse> getAllInstitutions(@PathVariable long id, @PathVariable String status) {
        if(status.equalsIgnoreCase("approved")) {
            institutionService.approveInstitution(id);
        } else if(status.equalsIgnoreCase("rejected")) {
            institutionService.rejectInstitution(id);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Invalid status provided", null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", null,
                        "Institution request status updated successfully. Generated user account has been sent to provided email address", null));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<StandardResponse> getAllApprovedInstitutions() {
        List<Institution> institutions = institutionService.getAllApprovedInstitutions();
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", institutions,
                        "Institutions retrieved successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getInstitutionById(@PathVariable Long id) {
        Institution institution = institutionService.getInstitutionById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", institution,
                        "Institution retrieved successfully", null));
    }

    @PreAuthorize("permitAll()")
    @PostMapping
    public ResponseEntity<StandardResponse> submitInstitutionRegistrationRequest(@Valid @RequestBody Institution institution) {
        Institution createdInstitution = institutionService.createInstitution(institution);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.createStandardResponse("success", createdInstitution,
                        "Institution registration request submitted successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateInstitution(@PathVariable Long id,
            @RequestBody Institution institution) {
        Institution updatedInstitution = institutionService.updateInstitution(id, institution);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", updatedInstitution,
                        "Institution updated successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(responseService.createStandardResponse("success", null,
                        "Institution deleted successfully", null));
    }
}
