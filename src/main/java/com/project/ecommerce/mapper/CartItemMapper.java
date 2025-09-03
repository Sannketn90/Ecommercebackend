package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.CartItemDTO;
import com.project.ecommerce.entity.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemDTO toDTO(CartItem cartItem);
    CartItem toEntity(CartItemDTO dto);
}
