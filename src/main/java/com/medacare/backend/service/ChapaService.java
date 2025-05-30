package com.medacare.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.medacare.backend.dto.ChapaRequestDto;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.User;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.repository.appointment.AvailabilitySlotRepository;
import com.yaphet.chapa.Chapa;
import com.yaphet.chapa.model.Customization;
import com.yaphet.chapa.model.InitializeResponseData;
import com.yaphet.chapa.model.PostData;
import com.yaphet.chapa.model.VerifyResponseData;
import com.yaphet.chapa.utility.Util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChapaService {
    private final Chapa chapa;
    private final UserRepository userRepository;
    private final ResponseService responseService;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public ChapaRequestDto sendRequestPayment(User customer, BigDecimal amount) throws Throwable {
        Customization customization = new Customization()
                .setTitle("MedaCare")
                .setDescription("MedaCare service payment")
                .setLogo(
                        "https://res.cloudinary.com/db6j8ag7i/image/upload/v1746983943/medacare_logo_transparent_uj8sjg.png");

        String txRef = "MedaCare_" + Util.generateToken();

        PostData postData = new PostData()
                .setAmount(amount)
                .setCurrency("ETB")
                .setFirstName(customer.getFirstName())
                .setLastName(customer.getLastName())
                .setEmail(customer.getEmail())
                .setTxRef(txRef)
                // .setCallbackUrl("https://medacare-be.onrender.com/payment/"+txRef)
                // .setReturnUrl("https://medacare-be.onrender.com/payment/"+txRef)
                .setSubAccountId("testSubAccountId")
                .setCustomization(customization);

        InitializeResponseData responseData = chapa.initialize(postData);
        ChapaRequestDto response = new ChapaRequestDto();
        response.setData(responseData);
        response.setTransactionRef(txRef);
        return response;
    }

    public Map<String, String> requestPayment(String email, BigDecimal amount) throws Throwable {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChapaRequestDto chapaResponse = sendRequestPayment(customer, amount);
        Map<String, String> response = new HashMap<>();
        if ((chapaResponse).getData().getStatus().equals("success")) {
            response.put("status", "success");
            response.put("url", chapaResponse.getData().getData().getCheckOutUrl());
            response.put("ref", chapaResponse.getTransactionRef());
        } else {
            response.put("status", "error");
        }
        return response;
    }

    // public void verifyPayment(String txRef) {
    // // Implement payment verification logic here
    // // You can use chapa.verify(txRef) to verify the payment status
    // }

    public VerifyResponseData verify(String paymentReference) throws Throwable {
        VerifyResponseData verifyResponseData = chapa.verify(paymentReference);
        return verifyResponseData;
    }
}
