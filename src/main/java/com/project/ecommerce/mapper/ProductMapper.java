package com.project.ecommerce.mapper;


import com.project.ecommerce.dto.ProductDTO;
import com.project.ecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Mapping(source = "user.id", target = "userId")
    ProductDTO toDTO(Product product);
}