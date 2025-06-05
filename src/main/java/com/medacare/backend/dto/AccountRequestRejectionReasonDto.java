package com.medacare.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestRejectionReasonDto {

    private boolean documentInvalid;
    private boolean licenseNotValid;
    private boolean identityUnverified;
    private boolean professionallyQualified;
    private String rejectionReasonNote;
}
