package com.example.springsddexample.util;

import com.example.springsddexample.model.dto.User;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;

import java.util.UUID;

public class UserTestUtils {

    public static User createActiveUserWithId(UUID id) {
        return User.builder()
                .id(id)
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
    
    public static UserEntity createActiveUserEntity(UUID testId) {
        return UserEntity.builder()
                .id(testId)
                .username(TestUtils.DEFAULT_USERNAME)
                .email(TestUtils.DEFAULT_EMAIL)
                .firstName(TestUtils.DEFAULT_FIRST_NAME)
                .lastName(TestUtils.DEFAULT_LAST_NAME)
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }

}