package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.User;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.service.ChapaService;
import com.medacare.backend.service.ResponseService;
import com.yaphet.chapa.model.InitializeResponseData;
import com.yaphet.chapa.model.VerifyResponseData;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping({ "/" })
@CrossOrigin
@RequiredArgsConstructor
public class GreetingsController {
    private final ChapaService chapaService;
    private final UserRepository userRepository;
    private final ResponseService responseService;
    private final ChapaService paymentService;

    @GetMapping("api/hello")
    public String sayHello() {
        return "Welcome to MedaCare API!";
    }

    @GetMapping("verify")
    public VerifyResponseData  getMethodName(@RequestParam String ref) throws Throwable{
        return paymentService.verify(ref);
    }
    
    
}
