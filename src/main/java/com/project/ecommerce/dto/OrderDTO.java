package com.project.ecommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private UserDTO user;
    private List<CartItemDTO> cartItems;
    private double totalAmount;
    private String status;
    private LocalDateTime orderDate;
}
