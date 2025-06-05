package com.medacare.backend.dto;

import java.util.List;
import java.util.Map;

import com.medacare.backend.dto.report.PatientGenderCountDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminDashboardDto {

    List<PatientGenderCountDto>  patientsByGender;
    private Long totalPatients;
    private Long totalPhysicians;
    private Long totalInstitutions;
    private Integer physiciansPendingApproval;
    private Integer institutionsPendingApproval;

}
