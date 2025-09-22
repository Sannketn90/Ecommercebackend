package com.project.ecommerce.serviceimpl;

import com.project.ecommerce.dto.LoginRequest;
import com.project.ecommerce.dto.SignupRequest;
import com.project.ecommerce.dto.UserResponse;
import com.project.ecommerce.entity.Role;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.DuplicateUserException;
import com.project.ecommerce.exception.InvalidCredentialsException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.mapper.UserMapper;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtUtil;
import com.project.ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    // ================= SIGNUP =================
    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.username")
    public UserResponse signup(SignupRequest request) {
        log.info("Signup attempt for username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw DuplicateUserException.forUsername(request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw DuplicateUserException.forEmail(request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User signed up successfully: {}", savedUser.getUsername());

        return userMapper.toResponse(savedUser);
    }

    // ================= LOGIN =================
    @Override
    public UserResponse login(LoginRequest request) throws AuthenticationException {
        log.info("Login attempt for username: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = findByUsername(request.getUsername());


        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        log.info("Login successful for user: {}", user.getUsername());

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }

    // ================= FIND BY USERNAME =================
    @Override
    @Cacheable(value = "users", key = "#username", unless = "#result == null")
    public User findByUsername(String username) {
        log.debug("Cache MISS → Fetching user by username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return ResourceNotFoundException.forUser(username);
                });
    }
}