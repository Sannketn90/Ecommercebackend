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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse signup(SignupRequest request) {
        log.info("Signup attempt for username: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Username already exists: {}", request.getUsername());
            throw DuplicateUserException.forUsername(request.getUsername());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        User savedUser = userRepository.save(user);
        log.info("User signed up successfully: {}", request.getUsername());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse login(LoginRequest request) throws AuthenticationException {
        log.info("Attempting login for user: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );
        } catch (AuthenticationException e) {

            log.error("Login failed for user: {}", request.getUsername(), e);

            throw new InvalidCredentialsException("Invalid username or password");
        }
        User user = findByUsername(request.getUsername());

        String token = jwtUtil.generateToken(request.getUsername(), user.getRole());
        log.info("Login successful for user: {}", request.getUsername());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setRole(null);
        userResponse.setToken(token);
        return userResponse;
    }


    @Override
    public User findByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return ResourceNotFoundException.forUser(username);
                });
    }
}