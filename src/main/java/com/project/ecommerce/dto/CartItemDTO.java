package com.project.ecommerce.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CartItemDTO {

    private UUID itemId;
    private String productName;
    private int quantity;
    private double priceSnapshot;
}