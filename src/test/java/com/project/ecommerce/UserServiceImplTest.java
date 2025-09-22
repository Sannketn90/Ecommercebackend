package com.project.ecommerce;

import com.project.ecommerce.dto.SignupRequest;
import com.project.ecommerce.dto.UserResponse;
import com.project.ecommerce.entity.Role;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.DuplicateUserException;
import com.project.ecommerce.mapper.UserMapper;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtUtil;
import com.project.ecommerce.serviceimpl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void testSignup_Success() {
        SignupRequest request = SignupRequest.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password123")
                .role(Role.USER)
                .build();

        User userEntity = User.builder()
                .userId(UUID.randomUUID())
                .username("user1")
                .email("user1@test.com")
                .password("encodedPassword")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        UserResponse userResponse = UserResponse.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .build();

        // Mocks
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        // Call service
        UserResponse result = userService.signup(request);

        // Assertions
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals("user1@test.com", result.getEmail());

        // Verify repository interactions
        verify(userRepository).existsByUsername("user1");
        verify(userRepository).existsByEmail("user1@test.com");
        verify(userRepository).save(userEntity);
    }


}
