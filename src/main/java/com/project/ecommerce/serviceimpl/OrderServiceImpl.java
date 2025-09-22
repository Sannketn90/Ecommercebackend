package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.OrderResponse;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.exception.CartEmptyException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.exception.UserNotFoundException;
import com.project.ecommerce.mapper.OrderMapper;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @CacheEvict(value = "cart", key = "#username")
    @Transactional
    public OrderResponse placeOrder(String username) {
        log.info("Initiating order placement for user: {}", username);

        // ===== USER VALIDATION =====
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException(username);
                });

        // ===== CART VALIDATION =====
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Cart not found for user: {}", username);
                    return new ResourceNotFoundException("Cart not found");
                });

        if (cart.getItems().isEmpty()) {
            log.warn("Order placement failed: Cart is empty for user {}", username);
            throw new CartEmptyException("Cannot place order: Cart is empty");
        }

        // ===== ORDER CREATION =====
        Order order = new Order();
        order.setUser(user);
        order.setPlacedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(cart.getTotalPrice());

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPriceSnapshot(cartItem.getPriceSnapshot());
            return item;
        }).toList();

        order.setItems(orderItems);

        Order savedOrder;
        try {
            savedOrder = orderRepository.save(order);
            log.info("Order placed successfully for user {}: Order ID {}", username, savedOrder.getOrderId());
        } catch (Exception e) {
            log.error("Order persistence failed for user {}", username, e);
            throw new RuntimeException("Order placement failed due to internal error");
        }

        // ===== CART CLEANUP =====
        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);
        log.debug("Cart cleared after order placement for user {}", username);

        // ===== RESPONSE MAPPING =====
        return orderMapper.toResponse(savedOrder);
    }
}
