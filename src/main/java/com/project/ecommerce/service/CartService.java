package com.project.ecommerce.service;


import com.project.ecommerce.dto.CartDTO;
import com.project.ecommerce.dto.CartRequest;
import com.project.ecommerce.dto.CartUpdateRequest;

import java.util.List;

public interface CartService {
    CartDTO addToCart(CartRequest request, String username);

    List<CartDTO> getCartByUsername(String username);

    CartDTO updateCart(CartUpdateRequest request, String username);

    void removeFromCart(Long cartId, String username);
}
