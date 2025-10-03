package com.tracksecure.common.exception;

import com.tracksecure.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DeviceNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleDeviceNotAvailable(DeviceNotAvailableException e, HttpServletRequest request){
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE,"Device not available",e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(DuplicateEventException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEvent(DuplicateEventException e, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT,"Duplicate event",e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPayload(InvalidPayloadException e, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST,"Invalid payload",e.getMessage(),request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND,"Resource not found",e.getMessage(),request.getRequestURI());
    }

    // Creating a helper method that acts like a small factory for ResponseEntity to reduce
    // duplication across exceptions handlers in globalexceptionhandler
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, String path){
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(response,status);
    }
}
