package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.AccountRequestRejectionReasonDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Institution.InstitutionRegistrationRequestStatus;
import com.medacare.backend.model.Patient;
import com.medacare.backend.service.InstitutionPhysicianService;
import com.medacare.backend.service.InstitutionService;
import com.medacare.backend.service.PhysicianService;
import com.medacare.backend.service.ResponseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Physician.AccountRequestStatus;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.UserRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/institutions")
@CrossOrigin
public class InstitutionController {

        private final PhysicianRepository physicianRepository;

        private final InstitutionService institutionService;
        private final ResponseService responseService;
        private final UserRepository userRepository;
        private final PhysicianService physicianService;
        private final InstitutionPhysicianService institutionPhysicianService;

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/pending/requests")
        public ResponseEntity<StandardResponse> getPendingInstitutions() {
                List<Institution> institutions = institutionService.getAllPendingInstitutions();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                institutions == null ? new ArrayList<>() : institutions,
                                                "Institutions retrieved", null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/requests/{id}/{status}")
        public ResponseEntity<StandardResponse> updateRequestStatus(@PathVariable long id, @PathVariable String status,
                        @RequestBody(required = true) AccountRequestRejectionReasonDto rejectionReason) {
                if (status.equalsIgnoreCase("approved")) {
                        institutionService.approveInstitution(id);
                } else if (status.equalsIgnoreCase("rejected")) {
                        if (rejectionReason == null ||
                                        (rejectionReason.isDocumentInvalid() &&
                                                        rejectionReason.isIdentityUnverified() &&
                                                        rejectionReason.isLicenseNotValid() &&
                                                        rejectionReason.isProfessionallyQualified() &&
                                                        rejectionReason.getRejectionReasonNote().isBlank())) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(responseService.createStandardResponse("error", null,
                                                                "Rejection reason is required", null));
                        }
                        institutionService.rejectInstitution(id, rejectionReason);
                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Invalid status provided", null));
                }
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", null,
                                                "Institution request status updated. Generated user account has been sent to provided email address",
                                                null));
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping
        public ResponseEntity<StandardResponse> getAllApprovedInstitutions() {
                List<Institution> institutions = institutionService.getAllApprovedInstitutions();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                institutions == null ? new ArrayList<>() : institutions,
                                                "Institutions retrieved", null));
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/all")
        public ResponseEntity<StandardResponse> getAllInstitutions() {
                List<Institution> institutions = institutionService.getAllInstitutions();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                institutions == null ? new ArrayList<>() : institutions,
                                                "Institutions retrieved", null));
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/{id}")
        public ResponseEntity<StandardResponse> getInstitutionById(@PathVariable Long id) {
                Institution institution = institutionService.getInstitutionById(id);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                institution == null || institution
                                                                .getRequestStatus() != InstitutionRegistrationRequestStatus.APPROVED
                                                                                ? null
                                                                                : institution,
                                                "Institution retrieved", null));
        }

        @PreAuthorize("permitAll()")
        @PostMapping(consumes = { "multipart/form-data" })
        public ResponseEntity<StandardResponse> submitInstitutionRegistrationRequest(
                        @Valid @ModelAttribute Institution institution) throws IOException {
                return institutionService.createInstitution(institution);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<StandardResponse> updateInstitution(@PathVariable Long id,
                        @RequestBody Institution institution) {
                Institution updatedInstitution = institutionService.updateInstitution(id, institution);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", updatedInstitution,
                                                "Institution updated", null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<StandardResponse> deleteInstitution(@PathVariable Long id) {
                institutionService.deleteInstitution(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .body(responseService.createStandardResponse("success", null,
                                                "Institution deleted", null));
        }

        @PreAuthorize("hasRole('ORG_ADMIN')")
        @GetMapping("/current")
        public Institution getCurrentInstitution() {
                Institution institution = institutionService.getCurrentInstitution();
                return institution;
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/physicians")
        public ResponseEntity<StandardResponse> getInstitutionPhysicians() {
                List<Physician> physician = institutionService.getInstitutionPhysicians();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                physician == null ? new ArrayList<>() : physician,
                                                "Physicians retrieved", null));
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/{institutionId}/physicians")
        public ResponseEntity<StandardResponse> getInstitutionPhysicians(@PathVariable("institutionId") Long institutionId) {
                List<Physician> physician = institutionPhysicianService.getInstitutionPhysiciansById(institutionId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                physician == null ? new ArrayList<>() : physician,
                                                "Physicians retrieved", null));
        }

        @PreAuthorize("hasRole('ORG_ADMIN')")
        @PostMapping(value = "/physicians", consumes = { "multipart/form-data" })
        public ResponseEntity<StandardResponse> addPhysician(@ModelAttribute Physician physician) throws IOException {
                if (userRepository.findByEmail(physician.getEmail()).isPresent()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Physician with this email already exists", null));
                }

                return physicianService.addInstitutionPhysician(physician);
        }
        
        @PreAuthorize("isAuthenticated()")
        @GetMapping("/patients")
        public ResponseEntity<StandardResponse> getInstitutionPatients() {
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                institutionService.getInstitutionPatients(),
                                                "Institution patients retrieved", null));
        }

        

        

}
