package com.medacare.backend.service;

import org.springframework.stereotype.Service;

import com.medacare.backend.dto.StandardResponse;

@Service
public class ResponseService {
    public StandardResponse createStandardResponse(String status, Object data, String message, String errors) {
        StandardResponse response = new StandardResponse();
        response.setStatus(status);
        response.setData(data);
        response.setMessage(message);
        response.setErrors(errors);
        return response;
    }

}
