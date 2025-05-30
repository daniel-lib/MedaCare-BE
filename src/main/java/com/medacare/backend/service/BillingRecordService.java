package com.medacare.backend.service;

import org.springframework.stereotype.Service;

import com.medacare.backend.dto.ChapaPaymentResponse;
import com.medacare.backend.model.BillingRecord;

@Service
public class BillingRecordService {

    public void saveBillingRecord(ChapaPaymentResponse paymentResponse){
        BillingRecord billingRecord = new BillingRecord();
        billingRecord.setEvent(paymentResponse.getEvent());
        billingRecord.setFirstName(paymentResponse.getFirstName());
        billingRecord.setLastName(paymentResponse.getLastName());
        billingRecord.setEmail(paymentResponse.getEmail());
        billingRecord.setMobile(paymentResponse.getMobile());
        billingRecord.setAmount(paymentResponse.getAmount());
        billingRecord.setCurrency(paymentResponse.getCurrency());
        billingRecord.setPaymentServiceProviderCharge(paymentResponse.getCharge());
        billingRecord.setStatus(paymentResponse.getStatus());
        billingRecord.setFailure_reason(paymentResponse.getFailureReason());
        billingRecord.setReference(paymentResponse.getReference());
        billingRecord.setType(paymentResponse.getType());
        billingRecord.setTx_ref(paymentResponse.getTxRef());
        billingRecord.setPayment_method(paymentResponse.getPaymentMethod());

        // Convert createdAt and updatedAt to OffsetDateTime if needed
        if (paymentResponse.getCreatedAt() != null) {
            billingRecord.setCreatedAt(paymentResponse.getCreatedAt());
        }
        if (paymentResponse.getUpdatedAt() != null) {
            billingRecord.setUpdatedAt(paymentResponse.getUpdatedAt());
        }
    }
}
