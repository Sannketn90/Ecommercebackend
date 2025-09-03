package com.project.ecommerce.service;


import com.project.ecommerce.dto.LoginRequest;
import com.project.ecommerce.dto.SignupRequest;
import com.project.ecommerce.dto.UserResponse;
import com.project.ecommerce.entity.User;

public interface UserService {

    UserResponse signup(SignupRequest request);

    UserResponse login(LoginRequest request);

    User findByUsername(String username);
}
