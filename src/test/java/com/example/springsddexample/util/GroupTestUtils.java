package com.example.springsddexample.util;

import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.enums.Status;

import java.util.UUID;

public class GroupTestUtils {
    
    public static final String DEFAULT_GROUP_NAME = "Test Group";
    public static final String DEFAULT_GROUP_DESCRIPTION = "Test group description";
    public static final Boolean DEFAULT_SELF_JOIN = false;
    public static final Boolean DEFAULT_SELF_LEAVE = false;
    
    public static Group createActiveGroup() {
        return Group.builder()
                .id(UUID.randomUUID())
                .name(DEFAULT_GROUP_NAME)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .admin(UserTestUtils.createActiveUser())
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }
    
    public static Group createActiveGroupWithId(UUID id) {
        return createActiveGroup().toBuilder()
                .id(id)
                .build();
    }
    
    public static Group createGroupWithCustomName(String name) {
        return createActiveGroup().toBuilder()
                .name(name)
                .build();
    }
    
    public static Group createGroupWithCustomAdmin(UUID adminId) {
        return createActiveGroup().toBuilder()
                .admin(UserTestUtils.createActiveUserWithId(adminId))
                .build();
    }
    
    public static Group createGroupForCreation() {
        return Group.builder()
                .name(DEFAULT_GROUP_NAME)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .admin(UserTestUtils.createActiveUser())
                .build();
    }
    
    public static Group createGroupForUpdate() {
        return Group.builder()
                .name("Updated Group")
                .description("Updated group description")
                .selfJoin(true)
                .selfLeave(true)
                .admin(UserTestUtils.createActiveUser())
                .build();
    }
    
    public static GroupEntity createActiveGroupEntity() {
        return GroupEntity.builder()
                .id(UUID.randomUUID())
                .name(DEFAULT_GROUP_NAME)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .admin(UserTestUtils.createActiveUserEntity())
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }
    
    public static GroupEntity createActiveGroupEntityWithId(UUID id) {
        return createActiveGroupEntity().toBuilder()
                .id(id)
                .build();
    }
    
    public static GroupEntity createGroupEntityWithCustomName(String name) {
        return createActiveGroupEntity().toBuilder()
                .name(name)
                .build();
    }
    
    public static GroupEntity createGroupEntityWithCustomAdmin(UUID adminId) {
        return createActiveGroupEntity().toBuilder()
                .admin(UserTestUtils.createActiveUserEntityWithId(adminId))
                .build();
    }
    
    public static String groupNameWithSuffix(String suffix) {
        return DEFAULT_GROUP_NAME + suffix;
    }
}