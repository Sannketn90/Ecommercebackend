package com.project.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is mandatory")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private double price;

    @Size(max = 500)
    private String description;

    private Long userId;
}
