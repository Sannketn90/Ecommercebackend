package com.project.ecommerce.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productName;

    private int quantity;

    private double priceSnapshot;
}