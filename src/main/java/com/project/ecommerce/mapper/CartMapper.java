package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.CartItemDTO;
import com.project.ecommerce.dto.CartResponse;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "product.name", target = "productName")
    CartItemDTO toDTO(CartItem item);

    default CartResponse toCartResponse(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
                .map(this::toDTO)
                .toList();

        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setItems(items);
        response.setTotalPrice(cart.getTotalPrice());
        return response;
    }
}