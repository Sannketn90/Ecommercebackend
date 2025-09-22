package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByUserUserId(UUID userId);

    Optional<Product> findByProductId(UUID productId);

    boolean existsByNameAndUserUserId(String name, UUID userId);

    List<Product> findByNameContainingIgnoreCase(String keyword);
}