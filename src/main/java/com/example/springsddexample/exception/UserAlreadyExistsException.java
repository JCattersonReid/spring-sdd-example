package com.example.springsddexample.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String field, String value) {
        super(field + " already exists: " + value);
    }
}