package com.medacare.backend.dto;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapaPaymentResponse {
    private String event;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String currency;
    private String amount;
    private String charge;
    private String status;
    private String failureReason;
    private String mode;
    private String reference;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String type;
    private String txRef;
    private String paymentMethod;
    private Customization customization;
    private Object meta;

    public static class Customization {
        private String title;
        private String description;
        private String logo;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}
