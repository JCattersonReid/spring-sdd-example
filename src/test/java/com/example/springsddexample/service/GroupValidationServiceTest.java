package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupAlreadyExistsException;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.repository.GroupRepository;
import com.example.springsddexample.util.GroupTestUtils;
import com.example.springsddexample.util.UserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupValidationServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupValidationService groupValidationService;

    private UUID testAdminId;
    private Group testGroup;
    private GroupEntity testGroupEntity;
    private UserEntity testAdminEntity;

    @BeforeEach
    void setUp() {
        testAdminId = UUID.randomUUID();
        testGroup = GroupTestUtils.createGroupForCreation(testAdminId);
        testAdminEntity = UserTestUtils.createActiveUserEntity(testAdminId);
        testGroupEntity = GroupTestUtils.createActiveGroupEntity(UUID.randomUUID(), testAdminEntity);
    }

    @Test
    void shouldValidateGroupCreationWhenNameIsUnique() {
        when(groupRepository.existsByNameAndStatusActive(testGroup.getName())).thenReturn(false);

        assertDoesNotThrow(() -> groupValidationService.validateGroupCreation(testGroup));
        verify(groupRepository).existsByNameAndStatusActive(testGroup.getName());
    }

    @Test
    void shouldThrowGroupAlreadyExistsExceptionWhenNameExists() {
        when(groupRepository.existsByNameAndStatusActive(testGroup.getName())).thenReturn(true);

        assertThrows(GroupAlreadyExistsException.class, () -> groupValidationService.validateGroupCreation(testGroup));
        verify(groupRepository).existsByNameAndStatusActive(testGroup.getName());
    }

    @Test
    void shouldValidateGroupUpdateWhenNameIsUnchanged() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        updateGroup.setName(testGroupEntity.getName());

        groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity);

        verifyNoInteractions(groupRepository);
    }

    @Test
    void shouldValidateGroupUpdateWhenNewNameIsUnique() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.existsByNameAndStatusActive(updateGroup.getName())).thenReturn(false);

        assertDoesNotThrow(() -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        verify(groupRepository).existsByNameAndStatusActive(updateGroup.getName());
    }

    @Test
    void shouldThrowGroupAlreadyExistsExceptionWhenUpdatingToExistingName() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.existsByNameAndStatusActive(updateGroup.getName())).thenReturn(true);

        assertThrows(GroupAlreadyExistsException.class, () -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        verify(groupRepository).existsByNameAndStatusActive(updateGroup.getName());
    }

    @Test
    void shouldNotValidateWhenNameIsNull() {
        Group groupWithNullName = GroupTestUtils.createGroupForCreation(testAdminId);
        groupWithNullName.setName(null);

        assertDoesNotThrow(() -> groupValidationService.validateGroupCreation(groupWithNullName));
        verifyNoInteractions(groupRepository);
    }
}