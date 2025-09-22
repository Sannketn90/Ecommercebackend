package com.project.ecommerce;

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
import com.project.ecommerce.serviceimpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private User owner;
    private User admin;
    private User anotherUser;
    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        owner = User.builder()
                .userId(UUID.randomUUID())
                .username("owner")
                .role(Role.USER)
                .build();

        admin = User.builder()
                .userId(UUID.randomUUID())
                .username("admin")
                .role(Role.ADMIN)
                .build();

        anotherUser = User.builder()
                .userId(UUID.randomUUID())
                .username("hacker")
                .role(Role.USER)
                .build();

        product = Product.builder()
                .productId(productId)
                .name("Old Product")
                .price(100.0)
                .description("Old Description")
                .user(owner)
                .build();

        request = ProductRequest.builder()
                .name("New Product")
                .price(200.0)
                .description("New Description")
                .build();
    }

    //  CASE 1: Owner updates successfully
    @Test
    void testUpdateProduct_ByOwner_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(new ProductDTO());

        ProductDTO result = productService.updateProduct(productId, request, "owner");

        assertNotNull(result);
        assertEquals("New Product", product.getName());
        assertEquals(200.0, product.getPrice());
        assertEquals("New Description", product.getDescription());

        verify(productRepository).save(product);
    }

    //  CASE 2: Admin updates successfully
    @Test
    void testUpdateProduct_ByAdmin_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(new ProductDTO());

        ProductDTO result = productService.updateProduct(productId, request, "admin");

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    //  CASE 3: Unauthorized user
    @Test
    void testUpdateProduct_UnauthorizedUser() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("hacker")).thenReturn(Optional.of(anotherUser));

        assertThrows(UnauthorizedActionException.class,
                () -> productService.updateProduct(productId, request, "hacker"));

        verify(productRepository, never()).save(any(Product.class));
    }

    //  CASE 4: Product not found
    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(productId, request, "owner"));
    }

    //  CASE 5: User not found
    @Test
    void testUpdateProduct_UserNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> productService.updateProduct(productId, request, "ghost"));
    }
}
