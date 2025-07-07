package com.rookies3.genaiquestionapp.exception;

import com.rookies3.genaiquestionapp.problem.controller.dto.ProblemDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDto.ApiResponseWrapper<Object>> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(
                ProblemDto.ApiResponseWrapper.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .error(ex.getClass().getSimpleName())
                        .timestamp(LocalDateTime.now())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDto.ApiResponseWrapper<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                ProblemDto.ApiResponseWrapper.builder()
                        .success(false)
                        .message("Validation failed: " + errorMessage)
                        .error(ex.getClass().getSimpleName())
                        .timestamp(LocalDateTime.now())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    // Generic exception handler for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDto.ApiResponseWrapper<Object>> handleAllUncaughtException(Exception ex) {
        return new ResponseEntity<>(
                ProblemDto.ApiResponseWrapper.builder()
                        .success(false)
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .error(ex.getClass().getSimpleName())
                        .timestamp(LocalDateTime.now())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
