package com.medacare.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medacare.backend.config.ApiPaths;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Physician;
import com.medacare.backend.service.PhysicianService;
import com.medacare.backend.service.ResponseService;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.BASE_API_VERSION + "/physicians")
public class PhysicianController {

    @Autowired
    private PhysicianService physicianService;

    @Autowired
    private ResponseService responseService;

    @GetMapping
    public ResponseEntity<StandardResponse> getAllPhysicians() {
        List<Physician> physicians = physicianService.getAllPhysicians();
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", physicians, "Physicians retrieved successfully", null, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getPhysicianById(@PathVariable Long id) {
        Physician physician = physicianService.getPhysicianById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", physician, "Physician retrieved successfully", null, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<StandardResponse> createPhysician(@RequestBody Physician physician) {
        Physician createdPhysician = physicianService.createPhysician(physician);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.createStandardResponse("success", createdPhysician, "Physician created successfully", null, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updatePhysician(@PathVariable Long id, @RequestBody Physician physician) {
        Physician updatedPhysician = physicianService.updatePhysician(id, physician);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", updatedPhysician, "Physician updated successfully", null, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deletePhysician(@PathVariable Long id) {
        physicianService.deletePhysician(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(responseService.createStandardResponse("success", null, "Physician deleted successfully", null, HttpStatus.NO_CONTENT));
    }
}