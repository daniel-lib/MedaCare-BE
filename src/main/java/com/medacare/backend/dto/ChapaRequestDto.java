package com.medacare.backend.dto;

import com.yaphet.chapa.model.InitializeResponseData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapaRequestDto {
    private InitializeResponseData data;
    private String transactionRef;
}
