package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserUsername(String username);

    Optional<Order> findByOrderIdAndUserUsername(UUID orderId, String username);
}