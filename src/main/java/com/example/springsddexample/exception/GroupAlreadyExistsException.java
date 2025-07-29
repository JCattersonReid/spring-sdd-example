package com.example.springsddexample.exception;

public class GroupAlreadyExistsException extends RuntimeException {
    
    public GroupAlreadyExistsException(String name) {
        super("Group already exists with name: " + name);
    }

}