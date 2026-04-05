package com.caloriestracker.system.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiErrorResponse response =
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        "VALIDATION_ERROR",
                        request.getRequestURI(),
                        errors
                );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request
    ) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        ex.getStatus().value(),
                        ex.getMessage(),
                        ex.getErrorCode(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unhandled exception", ex);

        ApiErrorResponse response =
                new ApiErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Unexpected server error",
                        "INTERNAL_SERVER_ERROR",
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}