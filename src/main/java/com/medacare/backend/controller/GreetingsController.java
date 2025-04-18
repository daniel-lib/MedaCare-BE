package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/"})
@CrossOrigin
public class GreetingsController {
    @GetMapping("api/hello")
    public String sayHello() {
        return "Welcome to MedaCare API!";
    }
}
