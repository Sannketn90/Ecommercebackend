package com.project.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private String username;
    private List<ProductDTO> products;
    private int quantity;
    private double totalPrice;
}
