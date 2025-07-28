package com.example.springsddexample.exception;

import java.util.UUID;

public class GroupNotFoundException extends RuntimeException {
    
    public GroupNotFoundException(UUID id) {
        super("Group not found with id: " + id);
    }

}