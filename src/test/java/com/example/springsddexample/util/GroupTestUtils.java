package com.example.springsddexample.util;

import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;

import java.util.UUID;

public class GroupTestUtils {

    public static final String DEFAULT_GROUP_NAME = "Test Group";
    public static final String DEFAULT_GROUP_DESCRIPTION = "A test group for unit testing";
    public static final Boolean DEFAULT_SELF_JOIN = false;
    public static final Boolean DEFAULT_SELF_LEAVE = false;

    public static Group createActiveGroup() {
        return Group.builder()
                .name(DEFAULT_GROUP_NAME)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .adminId(UUID.randomUUID())
                .build();
    }

    public static Group createActiveGroupWithId(UUID id, UUID adminId) {
        return Group.builder()
                .id(id)
                .name(DEFAULT_GROUP_NAME)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .adminId(adminId)
                .build();
    }

    public static Group createGroupWithCustomName(String name) {
        return createActiveGroup().toBuilder()
                .name(name)
                .build();
    }

    public static Group createGroupWithCustomNameAndAdmin(String name, UUID adminId) {
        return createActiveGroup().toBuilder()
                .name(name)
                .adminId(adminId)
                .build();
    }

    public static Group createGroupForUpdate() {
        return Group.builder()
                .name("Updated Group")
                .description("Updated group description")
                .selfJoin(true)
                .selfLeave(true)
                .adminId(UUID.randomUUID())
                .build();
    }

    public static GroupEntity createActiveGroupEntity(UUID testId, UserEntity admin) {
        return GroupEntity.builder()
                .id(testId)
                .name(DEFAULT_GROUP_NAME)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .admin(admin)
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }

    public static GroupEntity createGroupEntityWithCustomName(String name, UserEntity admin) {
        return GroupEntity.builder()
                .name(name)
                .description(DEFAULT_GROUP_DESCRIPTION)
                .selfJoin(DEFAULT_SELF_JOIN)
                .selfLeave(DEFAULT_SELF_LEAVE)
                .admin(admin)
                .status(Status.ACTIVE)
                .createdAt(TestUtils.fixedDateTime())
                .updatedAt(TestUtils.fixedDateTime())
                .build();
    }

    public static String groupNameWithSuffix(String suffix) {
        return DEFAULT_GROUP_NAME + suffix;
    }
}