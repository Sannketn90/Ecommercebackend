package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUser(User user);

    @Query("SELECT c FROM Cart c JOIN FETCH c.items WHERE c.user = :user")
    Optional<Cart> fetchCartWithItems(@Param("user") User user);
}