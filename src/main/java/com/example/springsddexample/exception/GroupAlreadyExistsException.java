package com.example.springsddexample.exception;

public class GroupAlreadyExistsException extends RuntimeException {
    
    public GroupAlreadyExistsException(String field, String value) {
        super(field + " already exists: " + value);
    }
}