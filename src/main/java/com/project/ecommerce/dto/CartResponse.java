package com.project.ecommerce.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartResponse {
    private UUID cartId;
    private List<CartItemDTO> items;
    private double totalPrice;
}