package com.example.springsddexample.util;

import com.example.springsddexample.model.dto.User;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;

import java.util.UUID;

public class UserTestUtils {
    
    public static User createActiveUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username(TestUtils.DEFAULT_USERNAME)
                .email(TestUtils.DEFAULT_EMAIL)
                .firstName(TestUtils.DEFAULT_FIRST_NAME)
                .lastName(TestUtils.DEFAULT_LAST_NAME)
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }
    
    public static User createActiveUserWithId(UUID id) {
        return createActiveUser().toBuilder()
                .id(id)
                .build();
    }
    
    public static User createUserWithCustomEmail(String email) {
        return createActiveUser().toBuilder()
                .email(email)
                .build();
    }
    
    public static User createUserWithCustomUsername(String username) {
        return createActiveUser().toBuilder()
                .username(username)
                .build();
    }
    
    public static User createUserForCreation() {
        return User.builder()
                .username(TestUtils.DEFAULT_USERNAME)
                .email(TestUtils.DEFAULT_EMAIL)
                .firstName(TestUtils.DEFAULT_FIRST_NAME)
                .lastName(TestUtils.DEFAULT_LAST_NAME)
                .build();
    }
    
    public static User createUserForUpdate() {
        return User.builder()
                .username("updatedUser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .build();
    }
    
    public static UserEntity createActiveUserEntity() {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .username(TestUtils.DEFAULT_USERNAME)
                .email(TestUtils.DEFAULT_EMAIL)
                .firstName(TestUtils.DEFAULT_FIRST_NAME)
                .lastName(TestUtils.DEFAULT_LAST_NAME)
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }
    
    public static UserEntity createActiveUserEntityWithId(UUID id) {
        return createActiveUserEntity().toBuilder()
                .id(id)
                .build();
    }
    
    public static UserEntity createUserEntityWithCustomUsername(String username) {
        return createActiveUserEntity().toBuilder()
                .username(username)
                .build();
    }
    
    public static UserEntity createUserEntityWithCustomEmail(String email) {
        return createActiveUserEntity().toBuilder()
                .email(email)
                .build();
    }
}