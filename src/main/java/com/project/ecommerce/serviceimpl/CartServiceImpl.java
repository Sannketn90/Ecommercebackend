package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.CartDTO;
import com.project.ecommerce.dto.CartRequest;
import com.project.ecommerce.dto.CartUpdateRequest;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.exception.UnauthorizedActionException;
import com.project.ecommerce.exception.UserNotFoundException;
import com.project.ecommerce.mapper.CartMapper;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CartMapper cartMapper;

    @Override
    public CartDTO addToCart(CartRequest request, String username) {
        log.info("Adding product ID: {} to cart for user: {}", request.getProductId(), username);

        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });

        // Find product
        Product product = productService.findById(request.getProductId());

        // Create new cart entry
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProducts(List.of(product));
        cart.setQuantity(request.getQuantity());
        cart.setTotalPrice(request.getQuantity() * product.getPrice());

        Cart savedCart = cartRepository.save(cart);
        log.info("Product {} added to cart successfully", request.getProductId());

        return cartMapper.toDTO(savedCart);
    }

    @Override
    public List<CartDTO> getCartByUsername(String username) {
        log.debug("Fetching cart for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        List<Cart> carts = cartRepository.findByUser(user);
        List<CartDTO> cartDTOs = new ArrayList<>();

        for (Cart cart : carts) {
            cartDTOs.add(cartMapper.toDTO(cart)); // ✅ entity → DTO
        }

        return cartDTOs;
    }

    @Override
    public CartDTO updateCart(CartUpdateRequest request, String username) {
        log.info("Updating cart item ID: {} with quantity: {} for user: {}", request.getCartId(), request.getQuantity(), username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });

        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", request.getCartId());
                    return new ResourceNotFoundException("Cart not found with ID: " + request.getCartId());
                });

        // Ownership check
        if (!cart.getUser().getId().equals(user.getId())) {
            log.error("Unauthorized update attempt by {} on cart {}", username, request.getCartId());
            throw new UnauthorizedActionException("You are not allowed to update this cart");
        }

        // Update quantity + price
        cart.setQuantity(request.getQuantity());
        double totalPrice = cart.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum() * request.getQuantity();
        cart.setTotalPrice(totalPrice);

        Cart updateCart = cartRepository.save(cart);
        log.info("Cart item updated successfully: {}", request.getCartId());

        return cartMapper.toDTO(updateCart);
    }

    @Override
    public void removeFromCart(Long cartId, String username) {
        log.info("Removing cart item ID: {} for user: {}", cartId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", cartId);
                    return new ResourceNotFoundException("Cart not found with ID: " + cartId);
                });

        // Ownership check
        if (!cart.getUser().getId().equals(user.getId())) {
            log.error("Unauthorized remove attempt by {} on cart {}", username, cartId);
            throw new UnauthorizedActionException("You are not allowed to remove this cart");
        }

        cartRepository.delete(cart);
        log.info("Cart item removed successfully: {}", cartId);
    }
}