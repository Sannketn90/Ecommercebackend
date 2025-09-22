package com.project.ecommerce;

import com.project.ecommerce.dto.OrderResponse;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.exception.CartEmptyException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.exception.UserNotFoundException;
import com.project.ecommerce.mapper.OrderMapper;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.serviceimpl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUsername("testuser");

        product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Laptop");
        product.setPrice(1000.0);

        CartItem cartItem = new CartItem();
        cartItem.setItemId(UUID.randomUUID());
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPriceSnapshot(1000.0);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(List.of(cartItem));
        cart.setTotalPrice(2000.0);
    }

    @Test
    void placeOrder_Success() {
        // arrange
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setUser(user);
        order.setTotalAmount(2000.0);

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setTotalAmount(2000.0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(response);

        // act
        OrderResponse result = orderService.placeOrder("testuser");

        // assert
        assertNotNull(result);
        assertEquals(2000.0, result.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void placeOrder_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> orderService.placeOrder("unknown"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_CartNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.placeOrder("testuser"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_EmptyCart_ThrowsException() {
        cart.setItems(List.of()); // empty cart

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        assertThrows(CartEmptyException.class,
                () -> orderService.placeOrder("testuser"));

        verify(orderRepository, never()).save(any());
    }
}
