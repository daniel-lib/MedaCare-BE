package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.AdminDashboardDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.PatientRepository;
import com.medacare.backend.model.Patient;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.service.InstitutionService;
import com.medacare.backend.service.PhysicianService;
import com.medacare.backend.service.ResponseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(FixedVars.BASE_API_VERSION + "/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ResponseService responseService;
    private final PatientRepository patientRepository;
    private final InstitutionService institutionService;
    private final PhysicianRepository physicianRepository;
    private final PhysicianService physicianService;
    private final InstitutionRepository institutionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public ResponseEntity<StandardResponse> geAdminDashboardReport() {
        
        AdminDashboardDto report = new AdminDashboardDto();
        report.setTotalInstitutions(institutionRepository.count());
        report.setTotalPatients(patientRepository.count());
        report.setTotalPhysicians(physicianRepository.count());
        report.setPhysiciansPendingApproval(physicianService.getAllPendingPhysician().size());
        report.setInstitutionsPendingApproval(institutionService.getAllPendingInstitutions().size());
        report.setPatientsByGender(patientRepository.findPatientsByGender());

        return ResponseEntity.ok()
                .body(responseService.createStandardResponse("success", report, "Admin dashboard data", null));
    }

    @PreAuthorize("hasRole('ORG_ADMIN')")
    @GetMapping("/admin/institution")
    public ResponseEntity<StandardResponse> geInstitutionDashboardReport() {
        
        AdminDashboardDto report = new AdminDashboardDto();
        report.setTotalInstitutions(institutionRepository.count());
        report.setTotalPatients(patientRepository.count());
        report.setTotalPhysicians(physicianRepository.count());
        report.setPhysiciansPendingApproval(physicianService.getAllPendingPhysician().size());
        report.setInstitutionsPendingApproval(institutionService.getAllPendingInstitutions().size());
        report.setPatientsByGender(patientRepository.findPatientsByGender());

        return ResponseEntity.ok()
                .body(responseService.createStandardResponse("success", report, "Admin dashboard data", null));
    }

    @PreAuthorize("hasRole('PHYSICIAN')")
    @GetMapping("/physician/dashboard")
    public ResponseEntity<StandardResponse> getPhysiciandasboardReport() {
        
        AdminDashboardDto report = new AdminDashboardDto();
        report.setTotalInstitutions(institutionRepository.count());
        report.setTotalPatients(patientRepository.count());
        report.setTotalPhysicians(physicianRepository.count());
        report.setPhysiciansPendingApproval(physicianService.getAllPendingPhysician().size());
        report.setInstitutionsPendingApproval(institutionService.getAllPendingInstitutions().size());
        report.setPatientsByGender(patientRepository.findPatientsByGender());

        return ResponseEntity.ok()
                .body(responseService.createStandardResponse("success", report, "Admin dashboard data", null));
    }
}
