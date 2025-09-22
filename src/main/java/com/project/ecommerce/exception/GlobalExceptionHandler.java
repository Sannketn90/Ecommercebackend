package com.project.ecommerce.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.project.ecommerce.apiresponse.ApiResponse;
import com.project.ecommerce.apiresponse.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // Duplicate User Exception
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateUserException(DuplicateUserException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    // Resource Not Found Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    // Invalid Credentials Exception
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedActionException ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "FORBIDDEN");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    //User Not Found Exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    // Invalid Token Exception
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    // Product Not Found Exception
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductNotFoundException(ProductNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    // Cart Item Not Found Exception
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartItemNotFoundException(CartItemNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.CART_ITEM_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    // Cart Empty Exception
    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartEmptyException(CartEmptyException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.CART_EMPTY, HttpStatus.BAD_REQUEST);
    }

    // Spring Security authentication failure
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {

        return buildErrorResponse("Invalid username or password", ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

    // Validation Exception
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        return buildErrorResponse(ex.getMessage(), ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unhandled exception occurred", ex); // ✅ full stacktrace console પર આવશે
        return buildErrorResponse("Something went wrong", ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFormat(InvalidFormatException ex) {
        String fieldName = ex.getPath().get(0).getFieldName();
        String msg = "Invalid format for field: " + fieldName + ". " +
                "Expected valid UUID.";
        return buildErrorResponse(msg, ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(String message, ErrorCode errorCode, HttpStatus status) {
        ApiResponse<Void> body = ApiResponse.error(message, errorCode);
        return new ResponseEntity<>(body, status);
    }


}
