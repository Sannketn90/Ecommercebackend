package com.project.ecommerce.service;

import com.project.ecommerce.dto.ProductDTO;
import com.project.ecommerce.dto.ProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductDTO addProduct(ProductRequest productRequest, String username);

    ProductDTO updateProduct(UUID productId, ProductRequest productRequest, String username);

    void deleteProduct(UUID productId, String username);

    List<ProductDTO> getAllProducts();

    ProductDTO getById(UUID productId);
}