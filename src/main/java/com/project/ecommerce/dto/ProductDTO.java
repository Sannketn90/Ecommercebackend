package com.project.ecommerce.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {

    private UUID productId;
    private String name;
    private double price;
    private String description;
    private UUID userId;
}
