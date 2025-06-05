package com.medacare.backend.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponse {
    String status;
    // @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object data;
    String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String errors;
}
