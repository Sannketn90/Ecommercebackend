package com.project.ecommerce.exception;



public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User not found with Username: " + username);
    }
}
