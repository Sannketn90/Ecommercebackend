package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.ecommerce.entity.Role;
import lombok.Data;


@Data
public class UserResponse {

    private Long id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Role role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
}

