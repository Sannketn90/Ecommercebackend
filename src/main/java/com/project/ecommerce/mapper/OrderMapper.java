package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CartItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "status", target = "status") // enum → string
    OrderDTO toDTO(Order order);

    @Mapping(source = "status", target = "status") // string → enum
    Order toEntity(OrderDTO orderDTO);
}
