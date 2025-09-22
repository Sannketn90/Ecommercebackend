package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.OrderItemDTO;
import com.project.ecommerce.dto.OrderResponse;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "product.name", target = "productName")
    OrderItemDTO toDTO(OrderItem item);

    default OrderResponse toResponse(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
                .map(this::toDTO).toList();

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setItems(items);
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setPlacedAt(order.getPlacedAt());
        return response;
    }
}