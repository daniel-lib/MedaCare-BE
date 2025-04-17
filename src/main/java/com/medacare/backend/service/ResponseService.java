package com.medacare.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.medacare.backend.dto.StandardResponse;

@Service
public class ResponseService {
    public StandardResponse createStandardResponse(String status, Object data, String message, String errors, HttpStatus statusCode) {
        StandardResponse response = new StandardResponse();
        response.setStatus(status);
        response.setData(data);
        response.setMessage(message);
        response.setErrors(errors);
        response.setCode(statusCode.value());
        return response;
    }

}
