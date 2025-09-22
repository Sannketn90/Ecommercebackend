package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.ProductDTO;
import com.project.ecommerce.dto.ProductRequest;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.Role;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    // ================= ADD PRODUCT =================
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO addProduct(ProductRequest request, String username) {
        log.info("Adding product: {} by user: {}", request.getName(), username);

        User user = getUserOrThrow(username);

        Product product = productMapper.toEntity(request);
        product.setUser(user);

        Product saved = productRepository.save(product);
        log.info("Product added successfully: {}", saved.getProductId());

        return productMapper.toDTO(saved);
    }

    // ================= UPDATE PRODUCT =================
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#productId"),
            @CacheEvict(value = "products", key = "'allProducts'")
    })
    public ProductDTO updateProduct(UUID productId, ProductRequest request, String username) {
        log.info("Updating product ID: {} by user: {}", productId, username);

        Product existing = getProductOrThrow(productId);
        User user = getUserOrThrow(username);

        if (!existing.getUser().getUserId().equals(user.getUserId()) && user.getRole() != Role.ADMIN) {
            log.warn("Unauthorized update attempt by user: {}", username);
            throw new UnauthorizedActionException("Not authorized to update this product");
        }

        existing.setName(request.getName());
        existing.setPrice(request.getPrice());
        existing.setDescription(request.getDescription());

        Product updated = productRepository.save(existing);
        log.info("Product updated: {}", updated.getProductId());

        return productMapper.toDTO(updated);
    }

    // ================= DELETE PRODUCT =================
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#productId"),
            @CacheEvict(value = "products", key = "'allProducts'")
    })
    public void deleteProduct(UUID productId, String username) {
        log.info("Deleting product ID: {} by user: {}", productId, username);

        Product product = getProductOrThrow(productId);
        User user = getUserOrThrow(username);

        if (!product.getUser().getUserId().equals(user.getUserId()) && user.getRole() != Role.ADMIN) {
            log.warn("Unauthorized delete attempt by user: {}", username);
            throw new UnauthorizedActionException("Not authorized to delete this product");
        }


        productRepository.deleteById(productId);
        log.info("Product deleted: {}", productId);
    }

    // ================= GET ALL PRODUCTS =================
    @Override
    @Cacheable(value = "products", key = "'allProducts'")
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.debug("Cache MISS → Fetching all products from DB");

        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::toDTO)
                .toList();

        log.info("Fetched {} products from DB", productDTOs.size());
        return productDTOs;
    }

    // ================= GET PRODUCT BY ID =================
    @Override
    @Cacheable(value = "products", key = "#productId")
    @Transactional(readOnly = true)
    public ProductDTO getById(UUID productId) {
        log.debug("Cache MISS → Fetching product DTO for ID: {}", productId);
        return productMapper.toDTO(getProductOrThrow(productId));
    }

    // ================= INTERNAL =================
    private Product getProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found: {}", productId);
                    return new ResourceNotFoundException("Product not found: " + productId);
                });
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException(username);
                });
    }
}