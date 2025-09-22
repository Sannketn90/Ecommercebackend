package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.CartRequest;
import com.project.ecommerce.dto.CartResponse;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.CartItemNotFoundException;
import com.project.ecommerce.exception.ProductNotFoundException;
import com.project.ecommerce.exception.UserNotFoundException;
import com.project.ecommerce.mapper.CartMapper;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Cacheable(value = "cart", key = "#username", unless = "#result == null")
    public CartResponse getCart(String username) {
        log.info("Fetching cart for user: {}", username);
        Cart cart = getOrCreateCart(username);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @CacheEvict(value = "cart", key = "#username")
    public CartResponse addToCart(String username, CartRequest request) {
        log.info("Adding product {} to cart for user: {}", request.getProductId(), username);

        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found: {}", request.getProductId());
                    return new ProductNotFoundException(request.getProductId());
                });

        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getProductId().equals(product.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            log.info("Updated quantity for existing item: {}", existingItem.getItemId());
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setPriceSnapshot(product.getPrice());
            cart.getItems().add(item);
            log.info("Added new item to cart: {}", product.getName());
        }

        recalculateTotal(cart);
        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartResponse(saved);
    }

    @Override
    @CacheEvict(value = "cart", key = "#username")
    public CartResponse updateItem(String username, UUID itemId, int quantity) {
        log.info("Updating item {} in cart for user: {}", itemId, username);

        Cart cart = getOrCreateCart(username);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", itemId);
                    return new CartItemNotFoundException(itemId);
                });

        item.setQuantity(quantity);
        recalculateTotal(cart);

        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartResponse(saved);
    }

    @Override
    @CacheEvict(value = "cart", key = "#username")
    public void removeItem(String username, UUID itemId) {
        log.info("Removing item {} from cart for user: {}", itemId, username);

        Cart cart = getOrCreateCart(username);
        boolean removed = cart.getItems().removeIf(i -> i.getItemId().equals(itemId));

        if (!removed) {
            log.warn("Attempted to remove non-existent item: {}", itemId);
            throw new CartItemNotFoundException(itemId);
        }

        recalculateTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    @CacheEvict(value = "cart", key = "#username")
    public void clearCart(String username) {
        log.info("Clearing cart for user: {}", username);
        Cart cart = getOrCreateCart(username);
        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException(username);
                });

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    log.info("Creating new cart for user: {}", username);
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalPrice(0);
                    return cartRepository.save(newCart);
                });
    }

    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
        log.debug("Recalculated cart total: {}", total);
    }
}