package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.ecommerce.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID userId;
    private String username;
    private String email;
    private Role role;
    private String token;
}