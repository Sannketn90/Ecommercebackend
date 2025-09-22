package com.project.ecommerce.service;

import com.project.ecommerce.dto.CartRequest;
import com.project.ecommerce.dto.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(String username);

    CartResponse addToCart(String username, CartRequest request);

    CartResponse updateItem(String username, UUID itemId, int quantity);

    void removeItem(String username, UUID itemId);

    void clearCart(String username);
}
