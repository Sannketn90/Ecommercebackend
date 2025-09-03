package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.CartDTO;
import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.dto.ProductDTO;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderStatus;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.mapper.OrderMapper;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String username) {
        log.info("Placing order for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<CartDTO> cartItems = cartService.getCartByUsername(username);
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty for user: " + username);
        }

        Order order = new Order();
        order.setUser(user);

        List<CartItem> orderItems = new ArrayList<>();
        for (CartDTO cart : cartItems) {
            for (ProductDTO product : cart.getProducts()) {
                CartItem item = new CartItem();
                item.setProductId(product.getId());
                item.setQuantity(cart.getQuantity());
                item.setPrice(product.getPrice());
                orderItems.add(item);
            }
        }

        order.setCartItems(orderItems);

        double totalAmount = orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderDate(LocalDateTime.now());

        // clear cart
        for (CartDTO cart : cartItems) {
            cartService.removeFromCart(cart.getId(), username);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully for user: {}", username);

        return orderMapper.toDTO(savedOrder);
    }

}
