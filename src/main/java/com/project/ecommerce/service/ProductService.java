package com.project.ecommerce.service;


import com.project.ecommerce.dto.ProductDTO;
import com.project.ecommerce.entity.Product;

import java.util.List;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, String username);

    ProductDTO updateProduct(Long id, ProductDTO productDTO, Long userId);

    void deleteProduct(Long id, Long userId);

    List<ProductDTO> getAllProducts();

    Product findById(Long id);

}
