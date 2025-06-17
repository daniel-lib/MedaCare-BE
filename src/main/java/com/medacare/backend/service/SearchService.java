package com.medacare.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Physician;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.PhysicianRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ResponseService responseService;
    private final PhysicianRepository physicianRepository;
    private final InstitutionRepository institutionRepository;

    public ResponseEntity<StandardResponse> search(String keyword) {
        List<Physician> physicians = physicianRepository.searchByKeyword(keyword);
        List<Institution> institutions = institutionRepository.searchByKeyword(keyword);

        Map<String, Object> results = new HashMap<>();
        results.put("physicians", physicians);
        results.put("institutions", institutions);

        StandardResponse response = new StandardResponse("success", results, "Search results", null);
        return ResponseEntity.ok(response);
    }
}
