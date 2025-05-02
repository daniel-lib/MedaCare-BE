package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.AccountRequestRejectionReasonDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.service.InstitutionService;
import com.medacare.backend.service.PhysicianService;
import com.medacare.backend.service.ResponseService;

import jakarta.validation.Valid;

import java.util.List;

import com.medacare.backend.model.Physician;
import com.medacare.backend.model.Physician.AccountRequestStatus;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.UserRepository;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/institutions")
@CrossOrigin
public class InstitutionController {

        private final PhysicianRepository physicianRepository;

        private final InstitutionService institutionService;
        private final ResponseService responseService;
        private final UserRepository userRepository;
        private final PhysicianService physicianService;

        public InstitutionController(InstitutionService institutionService, ResponseService responseService,
                        UserRepository userRepository, PhysicianService physicianService,
                        PhysicianRepository physicianRepository) {
                this.institutionService = institutionService;
                this.responseService = responseService;
                this.userRepository = userRepository;
                this.physicianService = physicianService;
                this.physicianRepository = physicianRepository;
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/pending/requests")
        public ResponseEntity<StandardResponse> getPendingInstitutions() {
                List<Institution> institutions = institutionService.getAllPendingInstitutions();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", institutions,
                                                "Institutions retrieved successfully", null));
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
                                                "Institution request status updated successfully. Generated user account has been sent to provided email address",
                                                null));
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
        public ResponseEntity<StandardResponse> submitInstitutionRegistrationRequest(
                        @Valid @RequestBody Institution institution) {
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

        @PreAuthorize("hasRole('ORG_ADMIN')")
        @GetMapping("/current")
        public Institution getCurrentInstitution() {
                Institution institution = institutionService.getCurrentInstitution();
                return institution;
        }

        @PreAuthorize("hasRole('ORG_ADMIN')")
        @GetMapping("/physicians")
        public List<Physician> getInstitutionPhysicians() {
                List<Physician> physician = institutionService.getInstitutionPhysicians();
                return physician;
        }

        @PreAuthorize("hasRole('ORG_ADMIN')")
        @PostMapping("/physicians")
        public ResponseEntity<StandardResponse> addPhysician(@RequestBody Physician physician) {
                if (userRepository.findByEmail(physician.getEmail()).isPresent()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Physician with this email already exists", null));
                }

                Institution institution = institutionService.getCurrentInstitution();

                physician.setHealthcareProvider(institution);

                physicianService.createInstitutionPhysicianAccount(physician, institution);

                physician.setOrgnanizationAffiliated(true);
                physician.setAccountRequestStatus(AccountRequestStatus.APPROVED);
                physicianRepository.save(physician);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(responseService.createStandardResponse("success", physician,
                                                "Physician added successfully", null));
        }

}
