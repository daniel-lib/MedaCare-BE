package com.medacare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.service.SearchService;

import lombok.RequiredArgsConstructor;

@RequestMapping(FixedVars.BASE_API_VERSION+"/search")
@RequiredArgsConstructor
@RestController
public class SearchController {
    private final SearchService searchService;

    @GetMapping("")
    public ResponseEntity<StandardResponse> search(@RequestParam("s") String keyword) {
        return searchService.search(keyword);        
    }

}
