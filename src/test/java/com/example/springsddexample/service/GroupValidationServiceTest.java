package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupAlreadyExistsException;
import com.example.springsddexample.exception.UserNotFoundException;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.repository.GroupRepository;
import com.example.springsddexample.repository.UserRepository;
import com.example.springsddexample.util.GroupTestUtils;
import com.example.springsddexample.util.UserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupValidationServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupValidationService groupValidationService;

    private UUID testGroupId;
    private UUID testAdminId;
    private Group testGroup;
    private GroupEntity testGroupEntity;
    private UserEntity testAdminEntity;

    @BeforeEach
    void setUp() {
        testGroupId = UUID.randomUUID();
        testAdminId = UUID.randomUUID();
        testGroup = GroupTestUtils.createActiveGroupWithId(testGroupId);
        testGroup.getAdmin().setId(testAdminId);
        testGroupEntity = GroupTestUtils.createActiveGroupEntityWithId(testGroupId);
        testAdminEntity = UserTestUtils.createActiveUserEntityWithId(testAdminId);
        testGroupEntity.setAdmin(testAdminEntity);
    }

    @Test
    void validateGroupCreation_WhenValidGroup_ShouldNotThrowException() {
        // Given
        when(groupRepository.existsByNameAndStatusActive(testGroup.getName())).thenReturn(false);
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE))
                .thenReturn(Optional.of(testAdminEntity));

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupCreation(testGroup));
        
        verify(groupRepository).existsByNameAndStatusActive(testGroup.getName());
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupCreation_WhenGroupNameAlreadyExists_ShouldThrowGroupAlreadyExistsException() {
        // Given
        when(groupRepository.existsByNameAndStatusActive(testGroup.getName())).thenReturn(true);

        // When & Then
        GroupAlreadyExistsException exception = assertThrows(
                GroupAlreadyExistsException.class,
                () -> groupValidationService.validateGroupCreation(testGroup)
        );
        
        assertEquals("Group name already exists: " + testGroup.getName(), exception.getMessage());
        verify(groupRepository).existsByNameAndStatusActive(testGroup.getName());
        verify(userRepository, never()).findByIdAndStatus(any(), any());
    }

    @Test
    void validateGroupCreation_WhenAdminDoesNotExist_ShouldThrowUserNotFoundException() {
        // Given
        when(groupRepository.existsByNameAndStatusActive(testGroup.getName())).thenReturn(false);
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> groupValidationService.validateGroupCreation(testGroup)
        );
        
        assertEquals("User not found with id: " + testAdminId, exception.getMessage());
        verify(groupRepository).existsByNameAndStatusActive(testGroup.getName());
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupUpdate_WhenNameUnchanged_ShouldOnlyValidateAdmin() {
        // Given
        Group updateGroup = Group.builder()
                .name(testGroupEntity.getName()) // Same name
                .admin(testGroup.getAdmin())
                .build();
        
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE))
                .thenReturn(Optional.of(testAdminEntity));

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        
        verify(groupRepository, never()).existsByNameAndStatusActiveAndIdNot(any(), any());
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupUpdate_WhenNameChanged_ShouldValidateNameUniqueness() {
        // Given
        String newName = "New Group Name";
        Group updateGroup = Group.builder()
                .name(newName)
                .admin(testGroup.getAdmin())
                .build();
        
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(newName, testGroupId))
                .thenReturn(false);
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE))
                .thenReturn(Optional.of(testAdminEntity));

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        
        verify(groupRepository).existsByNameAndStatusActiveAndIdNot(newName, testGroupId);
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupUpdate_WhenNewNameAlreadyExists_ShouldThrowGroupAlreadyExistsException() {
        // Given
        String newName = "Existing Group Name";
        Group updateGroup = Group.builder()
                .name(newName)
                .admin(testGroup.getAdmin())
                .build();
        
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(newName, testGroupId))
                .thenReturn(true);

        // When & Then
        GroupAlreadyExistsException exception = assertThrows(
                GroupAlreadyExistsException.class,
                () -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity)
        );
        
        assertEquals("Group name already exists: " + newName, exception.getMessage());
        verify(groupRepository).existsByNameAndStatusActiveAndIdNot(newName, testGroupId);
        verify(userRepository, never()).findByIdAndStatus(any(), any());
    }

    @Test
    void validateGroupUpdate_WhenAdminUnchanged_ShouldNotValidateAdmin() {
        // Given
        Group updateGroup = Group.builder()
                .name("New Name")
                .admin(testGroup.getAdmin()) // Same admin
                .build();
        
        when(groupRepository.existsByNameAndStatusActiveAndIdNot("New Name", testGroupId))
                .thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        
        verify(groupRepository).existsByNameAndStatusActiveAndIdNot("New Name", testGroupId);
        verify(userRepository, never()).findByIdAndStatus(any(), any());
    }

    @Test
    void validateGroupUpdate_WhenAdminChanged_ShouldValidateNewAdmin() {
        // Given
        UUID newAdminId = UUID.randomUUID();
        Group updateGroup = Group.builder()
                .name(testGroupEntity.getName())
                .admin(UserTestUtils.createActiveUserWithId(newAdminId))
                .build();
        
        when(userRepository.findByIdAndStatus(newAdminId, Status.ACTIVE))
                .thenReturn(Optional.of(UserTestUtils.createActiveUserEntityWithId(newAdminId)));

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        
        verify(userRepository).findByIdAndStatus(newAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupUpdate_WhenNewAdminDoesNotExist_ShouldThrowUserNotFoundException() {
        // Given
        UUID newAdminId = UUID.randomUUID();
        Group updateGroup = Group.builder()
                .name(testGroupEntity.getName())
                .admin(UserTestUtils.createActiveUserWithId(newAdminId))
                .build();
        
        when(userRepository.findByIdAndStatus(newAdminId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity)
        );
        
        assertEquals("User not found with id: " + newAdminId, exception.getMessage());
        verify(userRepository).findByIdAndStatus(newAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupCreation_WhenGroupNameIsNull_ShouldNotValidateName() {
        // Given
        Group groupWithNullName = testGroup.toBuilder().name(null).build();
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE))
                .thenReturn(Optional.of(testAdminEntity));

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupCreation(groupWithNullName));
        
        verify(groupRepository, never()).existsByNameAndStatusActive(any());
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
    }

    @Test
    void validateGroupUpdate_WhenAdminIsNull_ShouldNotValidateAdmin() {
        // Given
        Group updateGroup = Group.builder()
                .name("New Name")
                .admin(null)
                .build();
        
        when(groupRepository.existsByNameAndStatusActiveAndIdNot("New Name", testGroupId))
                .thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> groupValidationService.validateGroupUpdate(updateGroup, testGroupEntity));
        
        verify(groupRepository).existsByNameAndStatusActiveAndIdNot("New Name", testGroupId);
        verify(userRepository, never()).findByIdAndStatus(any(), any());
    }
}