package com.project.ecommerce.controller;


import com.project.ecommerce.apiresponse.ApiResponse;
import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.OrderService;
import com.project.ecommerce.service.ProductService;
import com.project.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "E-Commerce API", description = "APIs for user authentication, product management, cart operations, and order processing")
public class ApiController {

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;


    @Operation(summary = "User Signup", description = "Register a new user")
    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.signup(request), "User registered successfully"));
    }

    @Operation(summary = "User Login", description = "Authenticate a user and return a token")
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.login(request), "User logged in successfully"));
    }

    @Operation(summary = "add a new product (admin only)", description = "Add a new product to the catalog (admin only)")
    @PostMapping("/product/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(
            @Valid @RequestBody ProductDTO productDTO,
            Authentication authentication) {

        ProductDTO savedProduct = productService.addProduct(productDTO, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(savedProduct, "Product added successfully"));
    }

    @Operation(summary = "update a product (admin only)", description = "Update an existing product in the catalog (admin only)")
    @PutMapping("/product/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id,
                                                                 @Valid @RequestBody ProductDTO productDTO,
                                                                 Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO, user.getId());
        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Product updated successfully"));
    }

    @Operation(summary = "delete a product (admin only)", description = "Delete a product from the catalog (admin only)")
    @DeleteMapping("/product/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        productService.deleteProduct(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @Operation(summary = "get all products", description = "Fetch all products from the catalog")
    @GetMapping("/product/all")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @Operation(summary = "add item to cart", description = "Add an item to the user's cart")
    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@Valid @RequestBody CartRequest request, Authentication authentication) {

        CartDTO cartDTO = cartService.addToCart(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(cartDTO, "Item added to cart successfully"));
    }

    @Operation(summary = "get cart items", description = "Fetch all items in the user's cart")
    @GetMapping("/cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CartDTO>>> getCart(Authentication authentication) {
        List<CartDTO> cartByUsername = cartService.getCartByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(cartByUsername, "Cart fetched successfully"));
    }

    @Operation(summary = "update cart item", description = "Update the quantity of an item in the user's cart")
    @PutMapping("/cart/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CartDTO>> updateCart(@Valid @RequestBody CartUpdateRequest request, Authentication authentication) {
        CartDTO cartDTO = cartService.updateCart(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(cartDTO, "Cart item updated successfully"));
    }

    @Operation(summary = "remove cart item", description = "Remove an item from the user's cart")
    @DeleteMapping("/cart/remove/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Long cartId, Authentication authentication) {
        cartService.removeFromCart(cartId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart item removed successfully"));
    }

    @Operation(summary = "place order", description = "Place an order for the items in the user's cart")
    @PostMapping("/order/place")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(Authentication authentication) {
        OrderDTO orderDTO = orderService.placeOrder(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(orderDTO, "Order placed successfully"));
    }

}
