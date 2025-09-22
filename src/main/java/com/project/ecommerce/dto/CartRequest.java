package com.project.ecommerce.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class CartRequest {

    private UUID productId;

    @Positive(message = "Quantity must be positive")
    private int quantity;
}