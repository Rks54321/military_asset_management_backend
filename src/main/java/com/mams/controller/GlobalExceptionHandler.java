package com.mams.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handle(Exception e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage() == null ? "Request failed" : e.getMessage()));
    }
}