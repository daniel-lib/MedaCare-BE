package com.medacare.backend.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BillingRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String event;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String amount;
    private String currency;
    private String paymentServiceProviderCharge;
    private String status;
    private String failure_reason;
    private String reference;
    private OffsetDateTime paymentTimestamp;
    private String type;
    private String tx_ref;
    private String payment_method;
    private String reason;

    @CreationTimestamp
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
