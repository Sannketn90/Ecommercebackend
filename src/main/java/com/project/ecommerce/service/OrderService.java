package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(String username);
}
