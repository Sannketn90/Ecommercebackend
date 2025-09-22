package com.project.ecommerce.service;

import com.project.ecommerce.dto.PaymentResponse;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse processPayment(UUID orderId, String username);
}