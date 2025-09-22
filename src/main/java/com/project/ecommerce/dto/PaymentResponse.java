package com.project.ecommerce.dto;

import com.project.ecommerce.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String transactionId;
    private PaymentStatus status;
    private String message;
    private Double amount;
    private UUID orderId;
    private LocalDateTime paidAt;
    private String username;
}