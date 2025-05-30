package com.medacare.backend.controller;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.ChapaPaymentResponse;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.BillingRecord;
import com.medacare.backend.service.AppointmentService;
import com.medacare.backend.service.BillingRecordService;

import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(FixedVars.BASE_API_VERSION + "/appointments")
@RestController
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final BillingRecordService billingRecordService;

    @PreAuthorize("hasAnyRole('PHYSICIAN', 'PATIENT')")
    @GetMapping("/{entityId}")
    public ResponseEntity<StandardResponse> getAppointmentsByPatientAndPhysician(@PathVariable Long entityId) {
        return appointmentService.getAppointmentsByPatientAndPhysician(entityId);
    }

    @PostMapping("/payment-result")
    public ResponseEntity<String> receivePaymentWebhook(@RequestBody ChapaPaymentResponse paymentResponse) {
        String message = paymentResponse.getStatus().equals("success")?"Payment Complete":"There maybe a problem with the payment";
        String status = "medacare://payment-success?txnRef="+paymentResponse.getTxRef();
        if(paymentResponse.getStatus().equals("success")){
            billingRecordService.saveBillingRecord(paymentResponse);
        }
        else
        status = "medacare://payment-error?txnRef="+paymentResponse.getTxRef();
        
        String html = """
                <html>
                <head>
                    <title>%s<</title>
                    <script>
                        setTimeout(function() {
                            window.location = %s;
                            // window.location = "googlegmail://co";                            
                        }, 1000);

                        // fallback
                        setTimeout(function() {
                            window.location = "https://play.google.com/store/apps/details?id=com.medacare.app";
                        }, 3000);
                    </script>
                </head>
                <body>
                    <p>Redirecting you back to the app...</p>
                </body>
                </html>
                """.formatted(message, status);
       return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
        
    }

}
