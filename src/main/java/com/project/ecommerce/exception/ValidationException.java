package com.project.ecommerce.exception;

import com.project.ecommerce.apiresponse.ErrorCode;
import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {
    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
    }
}
