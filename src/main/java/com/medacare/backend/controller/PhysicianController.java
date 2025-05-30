package com.medacare.backend.controller;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy.Fixed;
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
import com.medacare.backend.model.Physician.AccountRequestStatus;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;
import com.medacare.backend.model.appointmentBooking.Calendar;
import com.medacare.backend.model.appointmentBooking.WorkingHoursWindow;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.service.*;
import com.yaphet.chapa.model.VerifyResponseData;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.medacare.backend.repository.UserRepository;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.models.media.MediaType;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(FixedVars.BASE_API_VERSION + "/physicians")
public class PhysicianController {

        private final AI_AssistanceService AI_AssistanceService;

        private final AppointmentService appointmentService;
        private final PhysicianRepository physicianRepository;
        private final PhysicianService physicianService;
        private final ResponseService responseService;
        private final AuthenticationService authenticationService;
        private final UserRepository userRepository;
        private final LocalFileUploadService cloudinaryService;
        private final CloudinaryFileUploadService cloudinaryFileUploadService;

        @PreAuthorize("isAuthenticated()")
        @GetMapping
        public ResponseEntity<StandardResponse> getApprovedPhysicians() {
                List<Physician> physicians = physicianService.getApprovedPhysicians();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                physicians == null ? new ArrayList<>() : physicians,
                                                "Physicians retrieved successfully",
                                                null));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/all")
        public ResponseEntity<StandardResponse> getAllPhysicians() {
                List<Physician> physicians = physicianService.getAllPhysicians();
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                physicians == null ? new ArrayList<>() : physicians,
                                                "Physicians retrieved successfully",
                                                null));
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/{id}")
        public ResponseEntity<StandardResponse> getPhysicianById(@PathVariable Long id) {
                Physician physician = physicianService.getPhysicianById(id);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                physician == null || physician
                                                                .getAccountRequestStatus() != AccountRequestStatus.APPROVED
                                                                                ? null
                                                                                : physician,
                                                "Physician retrieved",
                                                null));
        }

        @PreAuthorize("hasRole('PHYSICIAN')")
        @PostMapping(consumes = { "multipart/form-data" })
        public ResponseEntity<StandardResponse> createPhysician(@ModelAttribute Physician physician)
                        throws IOException {
                return physicianService.createPhysician(physician);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/pending/requests")
        public ResponseEntity<StandardResponse> getPendingPhysicianRequest() {
                List<Physician> physicians = physicianService.getAllPendingPhysician();
                physicians = physicians == null ? new ArrayList<>() : physicians;
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success",
                                                physicians == null ? new ArrayList<>() : physicians,
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

        @PreAuthorize("hasRole('PHYSICIAN')")
        // @PreAuthorize("permitAll()")
        @PostMapping(value = "/photo")
        public ResponseEntity<StandardResponse> uploadProfilePicture(@RequestParam("photo") MultipartFile photo)
                        throws IOException {
                if (photo.getSize() > 5_242_880)
                        throw new RuntimeException("File is too big. Size should not exceed 5MB");
                if (photo.isEmpty())
                        throw new RuntimeException("Please select a photo to upload");

                String contentType = photo.getContentType();
                if (contentType == null || !FixedVars.ALLOWED_IMAGE_TYPES.contains(contentType)) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                                        responseService.createStandardResponse("error", null, "Only images are allowed",
                                                        null));
                }

                String photoLink = "";
                try {
                        photoLink = cloudinaryFileUploadService.uploadFile(photo);
                        User currentUser = authenticationService.getCurrentUser();
                        currentUser.setPhotoLink(photoLink);
                        userRepository.save(currentUser);
                } catch (Exception ex) {
                        throw new RuntimeException("Could not upload photo");
                }
                return ResponseEntity.ok().body(responseService.createStandardResponse("success",
                                photoLink, "Profile picture uploaded successfully",
                                null));
        }

        @PreAuthorize("hasRole('PHYSICIAN')")
        @PostMapping("/work-hour")
        public ResponseEntity<StandardResponse> createWorkingHoursWindow(
                        @RequestBody WorkingHoursWindow workingWindow) {
                return physicianService.createWorkingHoursWindow(workingWindow);
        }

        @PreAuthorize("hasRole('PHYSICIAN')")
        @GetMapping("/work-hour")
        public ResponseEntity<StandardResponse> getWorkingHoursWindow() {
                Physician physician = physicianRepository.findByUser(authenticationService.getCurrentUser())
                                .orElseThrow(() -> new RuntimeException("Physician profile not found"));
                return physicianService.getWorkingHoursWindow(physician);
        }

        @PreAuthorize("hasRole('PHYSICIAN')")
        @DeleteMapping("/work-hour/{workHourId}")
        public ResponseEntity<StandardResponse> deleteWorkingHoursWindow(
                        @PathVariable("workHourId") Long workHourId) {
                Physician physician = physicianRepository.findByUser(authenticationService.getCurrentUser())
                                .orElseThrow(() -> new RuntimeException("Physician profile not found"));
                return physicianService.deleteWorkingHoursWindow(workHourId);
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/work-hour/{physicianId}")
        public ResponseEntity<StandardResponse> getWorkingHoursWindow(
                        @PathVariable("physicianId") long physicianId) {
                Physician physician = physicianRepository.findById(physicianId)
                                .orElseThrow(() -> new RuntimeException("Physician profile not found"));
                return physicianService.getWorkingHoursWindow(physician);

        }

        @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT', 'ADMIN')")
        @GetMapping("/{physicianId}/available/dates")
        public ResponseEntity<StandardResponse> getWorkingHoursWindowDates(
                        @PathVariable Long physicianId) {
                List<String> availableDates = appointmentService.getAvailableDates(physicianId);
                String message = availableDates.isEmpty() ? "No available dates found"
                                : "Available dates retrieved successfully";
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", availableDates,
                                                message, null));
        }

        @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT', 'ADMIN')")
        @GetMapping("/{id}/available/durations/{date}")
        public ResponseEntity<StandardResponse> getAvailableDurations(
                        @PathVariable Long id, @PathVariable String date) {
                List<Integer> availableDuration = appointmentService.getAvailableDuration(id, date);
                String message = availableDuration.size() == 0 ? "No available appointment duration found"
                                : "Appointment duration retrieved successfully";
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", availableDuration,
                                                message, null));
        }

        @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT', 'ADMIN')")
        @GetMapping("/{id}/available-slots/{date}/{duration}")
        public ResponseEntity<StandardResponse> getAvailableSlotForDate(
                        @PathVariable Long id, @PathVariable String date,
                        @PathVariable int duration) {
                Optional<Physician> physicianOpt = physicianRepository.findById(id);
                if (physicianOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Physician not found", null));
                }
                Physician physician = physicianOpt.get();
                duration = 30;

                List<AvailabilitySlot> availableSlot = appointmentService.getAvailableSlots(physician, duration, date);
                String message = availableSlot.isEmpty() ? "There are no available slots for this date"
                                : "Available slots retrieved successfully";
                return ResponseEntity.status(HttpStatus.OK)
                                .body(responseService.createStandardResponse("success", availableSlot,
                                                message, null));
        }

        @PreAuthorize("hasAnyRole('PATIENT')")
        @PostMapping("/book/slot/{id}")
        public ResponseEntity<StandardResponse> bookAppointmentSlot(
                        @PathVariable("id") Long slotId) throws Throwable {
                return appointmentService.bookAppointmentSlot(slotId);
        }

        @PreAuthorize("hasRole('PATIENT')")
        @PostMapping("/booking/finalization/{slotId}")
        public ResponseEntity<StandardResponse> finalizeAppointmentBooking(@PathVariable Long slotId) throws Throwable {
                // check for payment status
                // if payment is successful, finalize the appointment booking
                // else, return error response

                return appointmentService.finalizeAppointmentBooking(slotId);
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/verify/{slotId}")
        public ResponseEntity<StandardResponse> verify(@PathVariable("slotId") long slotId) throws Throwable {
                VerifyResponseData response = appointmentService.verify(slotId);
                if (response.getStatus().equals("success")) {
                        return ResponseEntity.ok()
                                        .body(responseService.createStandardResponse("success", null,
                                                        "Payment verified successfully", null));
                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Payment verification failed", null));
                }
        }

        @PreAuthorize("hasRole('PHYSICIAN')")
        @GetMapping("/appointments")
        public ResponseEntity<StandardResponse> getOwnAppointment() {
                Physician physician = physicianRepository.findByUser(authenticationService.getCurrentUser())
                                .orElseThrow(() -> new RuntimeException("Physician profile not found"));
                return ResponseEntity.ok()
                                .body(responseService.createStandardResponse("success",
                                                physicianService.getPhysicianAppointment(physician),
                                                "Appointments retrieved", null));
        }
}