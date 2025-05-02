package com.medacare.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestRejectionReasonDto {

    private boolean documentInvalid;
    private boolean licenseNotValid;
    private boolean identityUnverified;
    private boolean professionallyQualified;
    private String rejectionReasonNote;
}
