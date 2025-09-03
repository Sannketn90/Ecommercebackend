package com.project.ecommerce.exception;

import com.project.ecommerce.apiresponse.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException(String message) {
        super("Invalid or expired token", ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }
}
