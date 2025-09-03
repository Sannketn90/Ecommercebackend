package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);


}
