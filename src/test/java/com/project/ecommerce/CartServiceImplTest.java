package com.project.ecommerce;

import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.UserNotFoundException;
import com.project.ecommerce.mapper.CartMapper;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.serviceimpl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");

        CartItem item1 = new CartItem();
        item1.setItemId(UUID.randomUUID());
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setItemId(UUID.randomUUID());
        item2.setQuantity(1);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(item1, item2)));
        cart.setTotalPrice(500.0);
    }

    @Test
    void clearCart_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        cartService.clearCart("testuser");

        assertTrue(cart.getItems().isEmpty(), "Cart items should be empty after clear");
        assertEquals(0, cart.getTotalPrice(), "Total price should be reset to 0");
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void clearCart_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> cartService.clearCart("unknown"));

        verify(cartRepository, never()).save(any());
    }
}
