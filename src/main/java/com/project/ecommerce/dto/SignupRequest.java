package com.project.ecommerce.dto;


import com.project.ecommerce.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6)
    private String password;

    private Role role;
}

