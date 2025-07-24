package com.example.springsddexample.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }
    
    public UserNotFoundException(String identifier, String value) {
        super("User not found with " + identifier + ": " + value);
    }
}