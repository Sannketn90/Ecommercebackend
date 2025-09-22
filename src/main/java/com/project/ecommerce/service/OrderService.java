package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(String username);
}
