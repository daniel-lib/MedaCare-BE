package com.medacare.backend.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.medacare.backend.dto.StandardErrorResponse;
import com.medacare.backend.dto.StandardResponse;

@Service
public class ResponseService {
    public StandardResponse createStandardResponse(String status, Object data, String message, String errors,
            HttpStatus statusCode) {
        StandardResponse response = new StandardResponse();
        response.setStatus(status);
        response.setData(data);
        response.setMessage(message);
        response.setErrors(errors);
        return response;
    }

    public StandardErrorResponse createStandardErrorResponse(String status, String message,
            Map<String, String> errors) {
        StandardErrorResponse response = new StandardErrorResponse();
        response.setStatus(status);
        response.setMessage(message);
        response.setErrors(errors);
        return response;
    }

}
