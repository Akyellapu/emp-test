package com.qentelli.employeetrackingsystem.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.UNAUTHORIZED.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "Login failed", null);
        response.setErrorCode(HttpStatus.UNAUTHORIZED);
        response.setErrorDescription("Invalid username or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.NOT_FOUND.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "User lookup failed", null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription("User not found with the provided username");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<AuthResponse<Object>> handleDuplicateAccount(DuplicateAccountException ex) {
        logger.info("Duplicate account creation attempt: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.CONFLICT.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "Account creation failed", null);
        response.setErrorCode(HttpStatus.CONFLICT);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicatePersonException.class)
    public ResponseEntity<AuthResponse<Object>> handleDuplicatePerson(DuplicatePersonException ex) {
        logger.info("Duplicate person creation attempt: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.CONFLICT.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "person creation failed", null);
        response.setErrorCode(HttpStatus.CONFLICT);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateProjectException.class)
    public ResponseEntity<AuthResponse<Object>> handleDuplicateProject(DuplicateProjectException ex) {
        logger.info("Duplicate project creation attempt: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.CONFLICT.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "Project creation failed", null);
        response.setErrorCode(HttpStatus.CONFLICT);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleManagerNotFoundException(PersonNotFoundException ex, HttpServletRequest request) {
        logger.warn("person not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.NOT_FOUND.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "person not found for request path: " + request.getRequestURI(), null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult().getFieldErrors().stream().map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.joining(" | "));
        logger.debug("Validation failed: {}", errorMessages);
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.BAD_REQUEST.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), errorMessages, null);
        response.setErrorCode(HttpStatus.BAD_REQUEST);
        response.setErrorDescription("Validation failure");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "An unexpected error occurred", null);
        response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<AuthResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        AuthResponse<Void> response = new AuthResponse<>(HttpStatus.BAD_REQUEST.value(), RequestProcessStatus.FAILURE, String.format("Missing required request parameter: '%s'", ex.getParameterName()));
        response.setTimestamp(LocalDateTime.now());
        response.setErrorCode(HttpStatus.BAD_REQUEST);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AuthResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        AuthResponse<Void> response = new AuthResponse<>(HttpStatus.BAD_REQUEST.value(), RequestProcessStatus.FAILURE, String.format("Invalid format for parameter '%s': value '%s' is not valid. Expected format: yyyy-MM-dd", ex.getName(), String.valueOf(ex.getValue())));
        response.setTimestamp(LocalDateTime.now());
        response.setErrorCode(HttpStatus.BAD_REQUEST);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestDateException.class)
    public ResponseEntity<AuthResponse<Void>> handleMissingDateParam(MissingRequestDateException ex) {
        AuthResponse<Void> response = new AuthResponse<>(HttpStatus.BAD_REQUEST.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), String.format("Missing required parameter: '%s'", ex.getParameterName()), null);
        response.setErrorCode(HttpStatus.BAD_REQUEST);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReleaseNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleReleaseNotFoundException(ReleaseNotFoundException ex, HttpServletRequest request) {
        logger.warn("Release not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Release not found for request path: " + request.getRequestURI(), null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(TechStackResourceNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleTechStackResourceNotFound(TechStackResourceNotFoundException ex, HttpServletRequest request) {
        logger.warn("TechStack resource not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), "TechStack resource not found for path: " + request.getRequestURI(), null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ProjectResourceNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleProjectResourceNotFound(ProjectResourceNotFoundException ex, HttpServletRequest request) {
        logger.warn("Project resource not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Project resource not found for path: " + request.getRequestURI(), null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Resource not found for path: " + request.getRequestURI(), null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(SprintNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleSprintNotFound(SprintNotFoundException ex, HttpServletRequest request) {
        logger.warn("Sprint not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.NOT_FOUND.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "Sprint not found for request path: " + request.getRequestURI(), null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<AuthResponse<Object>> handleReportNotFound(ReportNotFoundException ex) {
        logger.warn("ProgressReport not found: {}", ex.getMessage());
        AuthResponse<Object> response = new AuthResponse<>(HttpStatus.NOT_FOUND.value(), RequestProcessStatus.FAILURE, LocalDateTime.now(), "Progress report lookup failed", null);
        response.setErrorCode(HttpStatus.NOT_FOUND);
        response.setErrorDescription(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}