package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.AccountRequestRejectionReasonDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.service.AuthenticationService;
import com.medacare.backend.service.CloudinaryService;
import com.medacare.backend.service.PhysicianService;
import com.medacare.backend.service.ResponseService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.medacare.backend.repository.UserRepository;

import io.swagger.v3.oas.models.media.MediaType;
import jakarta.mail.Multipart;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/physicians")
public class PhysicianController {

        private final PhysicianRepository physicianRepository;

        private final PhysicianService physicianService;
        private final ResponseService responseService;
        private final AuthenticationService authenticationService;
        private final UserRepository userRepository;
        private final CloudinaryService cloudinaryService;

        public PhysicianController(PhysicianService physicianService, ResponseService responseService,
                        AuthenticationService authenticationService, UserRepository userRepository,
                        PhysicianRepository physicianRepository, CloudinaryService cloudinaryService) {
                this.physicianService = physicianService;
                this.responseService = responseService;
                this.authenticationService = authenticationService;
                this.physicianRepository = physicianRepository;
                this.userRepository = userRepository;
                this.cloudinaryService = cloudinaryService;
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

                User physicianUser = authenticationService.getCurrentUser();
                physician.setUser(physicianUser);

                if (physicianRepository.existsByUserId(userId)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", physician,
                                                        "Physician info already saved", null));
                }
                if (physician.getDateOfBirth().isAfter(LocalDate.now())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Date of birth cannot be in the future", null));
                }
                physicianUser.setFirstLogin(false);

                Physician createdPhysician = physicianService.createPhysician(physician);
                userRepository.save(physicianUser);
                System.out.println(physicianUser.getFirstName());
                System.out.println(physicianUser.getLastName());

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(responseService.createStandardResponse("success", createdPhysician,
                                                "Physician profile has been submitted. Awaiting review.", null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/pending/requests")
        public ResponseEntity<StandardResponse> getPendingPhysicianRequest() {
                List<Physician> physicians = physicianService.getAllPendingPhysician();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", physicians,
                                                "Physician retrieved successfully", null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/requests/{id}/{status}")
        public ResponseEntity<StandardResponse> updateRequestStatus(@PathVariable long id, @PathVariable String status,
                        @RequestBody(required = false) AccountRequestRejectionReasonDto rejectionReason) {
                if (status.equalsIgnoreCase("approved")) {
                        physicianService.approvepPhysician(id);
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
                        physicianService.rejectPhysician(id, rejectionReason);
                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Invalid status provided", null));
                }
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", null,
                                                "Physician request status updated successfully",
                                                null));
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

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/specialty/{specialty}")
        public ResponseEntity<StandardResponse> getPhysicianBySpecialiy(@PathVariable("specialty") String specialty) {
                List<Physician> physiciansBySpecialization = physicianRepository.findBySpecialization(specialty);
                String responseMsg = physiciansBySpecialization.size() > 0 ? "Physicians fetched by speciality"
                                : "No Physician found for that speciality";

                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", physiciansBySpecialization,
                                                responseMsg, null));
        }

        // @PreAuthorize("hasRole('PHYSICIAN')")
        @PreAuthorize("permitAll()")
        @PostMapping(value = "/photo")
        public ResponseEntity<StandardResponse> uploadProfilePicture(@RequestParam("photo") MultipartFile photo)
                        throws IOException {
                return cloudinaryService.uploadProfPhoto(photo);
        }
}