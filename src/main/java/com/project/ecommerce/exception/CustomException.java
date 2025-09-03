package com.project.ecommerce.exception;

import com.project.ecommerce.apiresponse.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final String message;
    private final ErrorCode errorCode;
    private final HttpStatus status;

}
