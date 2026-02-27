package com.osc.leaderboard.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@ControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleBadRequestException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("A server error occured", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
