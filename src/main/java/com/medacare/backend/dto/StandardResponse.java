package com.medacare.backend.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponse {
    String status;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Object data;
    String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String errors;
}
