package com.project.ecommerce.dto;

import com.project.ecommerce.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID orderId;
    private List<OrderItemDTO> items;
    private double totalAmount;
    private OrderStatus status;
    private LocalDateTime placedAt;
}