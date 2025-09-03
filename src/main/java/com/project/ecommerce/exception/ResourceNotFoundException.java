package com.project.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super("Resource not found: " + message);
    }
    public static ResourceNotFoundException forUser(String username) {
        return new ResourceNotFoundException("User not found " + username);

    }
}
