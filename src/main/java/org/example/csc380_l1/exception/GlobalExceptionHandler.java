package org.example.csc380_l1.exception;

import org.example.csc380_l1.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        List<FieldError> details = result.getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorDetail error = ErrorDetail.builder()
                .code("VALIDATION_ERROR")
                .message("Request validation failed")
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(error));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
        Map<String, HateoasLink> links = new HashMap<>();
        links.put("games", HateoasLink.of("/v1/games"));

        ErrorDetail error = ErrorDetail.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                ._links(links)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(error));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedException ex) {
        Map<String, HateoasLink> links = new HashMap<>();
        links.put("login", HateoasLink.of("/v1/login", "POST"));

        ErrorDetail error = ErrorDetail.builder()
                .code("UNAUTHORIZED")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                ._links(links)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(error));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbidden(ForbiddenException ex) {
        ErrorDetail error = ErrorDetail.builder()
                .code("FORBIDDEN")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(error));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(ConflictException ex) {
        Map<String, HateoasLink> links = new HashMap<>();
        links.put("login", HateoasLink.of("/v1/login", "POST"));

        ErrorDetail error = ErrorDetail.builder()
                .code("CONFLICT")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                ._links(links)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericError(Exception ex) {
        ErrorDetail error = ErrorDetail.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(error));
    }
}

