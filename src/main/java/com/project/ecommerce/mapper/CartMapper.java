package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.CartDTO;
import com.project.ecommerce.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    @Mapping(source = "user.username", target = "username")
    CartDTO toDTO(Cart cart);

    @Mapping(source = "username", target = "user.username")
    Cart toEntity(CartDTO dto);
}
