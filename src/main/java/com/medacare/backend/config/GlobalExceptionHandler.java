package com.medacare.backend.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;
import java.net.SocketException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.MethodNotAllowed;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.medacare.backend.dto.StandardErrorResponse;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.service.ResponseService;
import com.yaphet.chapa.exception.ChapaException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseService responseService;

    public GlobalExceptionHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseService.createStandardErrorResponse(
                        "error",
                        "Validation failed",
                        errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleSecurityException(Exception exception) {

        StandardResponse response = new StandardResponse();
        response.setStatus("error");
        response.setMessage("Unknown internal error");
        exception.printStackTrace();
        int statusCode = 500;

        // if(exception instanceof MethodArgumentNotValidException){
        // statusCode = 400;
        // response.setMessage("Invalid input data");
        // }

        if (exception instanceof RuntimeException) {
            statusCode = 400;
            response.setMessage(exception.getMessage());
        }

        if (exception instanceof JwtException) {
            statusCode = 403;
            response.setMessage("Invalid JWT Token");
        }

        if (exception instanceof BadCredentialsException) {
            response.setMessage("The email or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        if (exception instanceof HttpMessageNotReadableException) {
            response.setMessage("Incorrect input data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        if (exception instanceof AccessDeniedException) {
            statusCode = 403;
            response.setMessage("You're not authorized to access this resource");
        }

        if (exception instanceof BadRequestException) {
            statusCode = 400;
            response.setMessage("Incorrect input data");
        }
        if (exception instanceof IllegalArgumentException) {
            statusCode = 400;
            response.setMessage(exception.getMessage());
        }

        if (exception instanceof AccountStatusException) {
            statusCode = 403;
            response.setMessage("The account is locked");
        }

        if (exception instanceof SignatureException) {
            statusCode = 403;
            response.setMessage("The JWT signature is invalid");
        }
        if (exception instanceof MalformedJwtException) {
            statusCode = 403;
            response.setMessage("Malformed JWT token");
        }

        if (exception instanceof ExpiredJwtException) {
            statusCode = 403;
            response.setMessage("The JWT token has expired");
        }

        if (exception instanceof NoResourceFoundException || exception instanceof NoHandlerFoundException) {
            statusCode = 404;
            response.setMessage("No resource found");
        }
        if (exception instanceof DateTimeParseException) {
            statusCode = 400;
            response.setMessage("Invalid date format");
        }
        if (exception instanceof HttpRequestMethodNotSupportedException || exception instanceof MethodNotAllowed) {
            statusCode = 405;
            response.setMessage("Method not allowed");
        }
        if (exception instanceof IOException) {
            statusCode = 400;
            response.setMessage(exception.getMessage());
        }
        if (exception instanceof ResponseStatusException) {
            statusCode = 400;
            response.setMessage(exception.getMessage());
        }

         if (exception instanceof HttpMediaTypeNotSupportedException) {
            statusCode = 400;
            response.setMessage("Unsupported media type (content type)");
        }
        if (exception instanceof ChapaException) {
            statusCode = 403;
            response.setMessage(exception.getMessage());
        }
        if(exception instanceof ChapaException) {
            statusCode = 400;
            String[] msgArr = exception.getMessage().split(",\\\"message\\\":\\\"");
            String[] msgArr2 = msgArr[1].split("\\\",\\\"status\\\":\\\"");
            response.setMessage(msgArr2[0]);
        }
        if(exception instanceof SocketException){
            statusCode = 500;
            response.setMessage("Connection error");
        }
        if(exception instanceof MissingServletRequestParameterException){
            statusCode = 400;
            response.setMessage(exception.getMessage());
        }
        if(exception instanceof ResourceNotFoundException){
            statusCode = 400;
            response.setMessage(exception.getMessage());
        }
        // if(exception instanceof Throwable){
        //     statusCode = 500;
        //     response.setMessage(exception.getMessage());
        // }


        return ResponseEntity.status(statusCode)
                .body(response);
    }
}
