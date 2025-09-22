package com.project.ecommerce.apiresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {


    private T data;
    private String message;
    private ErrorCode errorCode;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()

                .data(data)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .message(message)
                .errorCode(errorCode)
                .build();
    }

}