package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrderOrderId(UUID orderId);

    Optional<Payment> findByTransactionId(String transactionId);
}