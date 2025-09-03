package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.ProductDTO;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.exception.UnauthorizedActionException;
import com.project.ecommerce.exception.UserNotFoundException;
import com.project.ecommerce.mapper.ProductMapper;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {



    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, String username) {
        log.info("Adding product: {} by user ID: {}", productDTO.getName(), username);

        Optional<User> byUsername = userRepository.findByUsername(username);
        User user = byUsername.orElseThrow(() -> {
            log.error("User not found with username: {}", username);
            return new UserNotFoundException(username);
        });

        Product product = productMapper.toEntity(productDTO);
        product.setUser(user);

        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully: {}", productDTO.getName());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, Long userId) {
        log.info("Updating product with ID: {} by user ID: {}", id, userId);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product ID not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found with ID: " + id);
                });
        if (!existing.getUser().getId().equals(userId)) {
            log.error("User ID: {} not authorized to update product ID: {}", userId, id);
            throw new UnauthorizedActionException("Not authorized to update this product");
        }
        existing.setName(productDTO.getName());
        existing.setPrice(productDTO.getPrice());
        existing.setDescription(productDTO.getDescription());
        Product updatedProduct = productRepository.save(existing);

        log.info("Product updated successfully: {}", id);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id, Long userId) {
        log.info("Deleting product with ID: {} by user ID: {}", id, userId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found" + id);
                });
        if (!product.getUser().getId().equals(userId)) {
            log.warn("Unauthorized attempt: User ID '{}' tried to delete product ID '{}'", userId, id);
            throw new UnauthorizedActionException("Not authorized to delete this product");
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully: {}", id);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    public Product findById(Long id) {
        log.debug("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found"+ id);
                });
    }


}
