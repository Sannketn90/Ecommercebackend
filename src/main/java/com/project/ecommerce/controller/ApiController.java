package com.project.ecommerce.controller;

import com.project.ecommerce.apiresponse.ApiResponse;
import com.project.ecommerce.dto.*;
import com.project.ecommerce.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User & Product APIs")
@Validated
public class ApiController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    // ===== AUTH =====

    @Operation(summary = "User Signup")
    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @Operation(summary = "User Login")
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User logged in successfully"));
    }

    // ===== PRODUCTS =====

    @Operation(summary = "Create a new product (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductRequest productRequest,
            Authentication auth) {

        ProductDTO saved = productService.addProduct(productRequest, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Product created successfully"));
    }

    @Operation(summary = "Get all products")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @Operation(summary = "Get product by ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable UUID id) {
        ProductDTO product = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product fetched successfully"));
    }

    @Operation(summary = "Update a product (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest productRequest,
            Authentication auth) {

        ProductDTO updated = productService.updateProduct(id, productRequest, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(updated, "Product updated successfully"));
    }

    @Operation(summary = "Delete a product (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id, Authentication auth) {
        productService.deleteProduct(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
    // ===== CARTS =====

    @Operation(summary = "Get current user's cart")
    @GetMapping("/carts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication auth) {
        CartResponse response = cartService.getCart(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Cart fetched successfully"));
    }

    @Operation(summary = "Add product to cart")
    @PostMapping("/carts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody CartRequest request, Authentication auth) {
        CartResponse response = cartService.addToCart(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product added to cart"));
    }

    @Operation(summary = "Update quantity of a cart item")
    @PutMapping("/carts/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(@PathVariable UUID itemId,
                                                                @RequestParam int quantity,
                                                                Authentication auth) {
        CartResponse response = cartService.updateItem(auth.getName(), itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(response, "Cart item updated"));
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/carts/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable UUID itemId, Authentication auth) {
        cartService.removeItem(auth.getName(), itemId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Clear entire cart")
    @DeleteMapping("/carts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication auth) {
        cartService.clearCart(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared"));
    }

    // ===== PLACE ORDERS =====
    @Operation(summary = "Place an order from cart")
    @PostMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(Authentication auth) {
        OrderResponse response = orderService.placeOrder(auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Order placed successfully"));
    }
    // ==== PAYMENT =====
    @Operation(summary = "Initiate payment for an order")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(@PathVariable UUID orderId, Authentication auth) {
        PaymentResponse paymentResponse = paymentService.processPayment(orderId, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(paymentResponse, "Payment successful"));
    }


}