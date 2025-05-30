package com.medacare.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Payment;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment payCareProvider(Appointment appointment, BigDecimal percentage) {
        Physician physician = appointment.getPhysician();
        Payment payment = new Payment();
        if (physician.getOrgnanizationAffiliated() != null &&
                physician.getOrgnanizationAffiliated() == true &&
                physician.getHealthcareProvider() != null) {
            payment.setInstitution(physician.getHealthcareProvider());
        }

        // BigDecimal serviceFee = appointment.getServiceFee(); // Ensure this field exists
        BigDecimal serviceFee = new BigDecimal(50); // Ensure this field exists
        
        BigDecimal amountToPay = serviceFee.multiply(percentage);

        payment.setPhysician(physician);
        payment.setAppointment(appointment);
        payment.setAmountPaid(amountToPay);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PAID");

        return paymentRepository.save(payment);
    }
}
