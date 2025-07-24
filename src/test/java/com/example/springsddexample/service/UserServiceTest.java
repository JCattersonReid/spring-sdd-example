package com.example.springsddexample.service;

import com.example.springsddexample.exception.UserNotFoundException;
import com.example.springsddexample.model.assembler.UserAssembler;
import com.example.springsddexample.model.dto.User;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.repository.UserRepository;
import com.example.springsddexample.util.UserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAssembler userAssembler;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserService userService;

    private UUID testId;
    private User testUser;
    private UserEntity testUserEntity;
    private List<UserEntity> testUserEntities;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testUser = UserTestUtils.createActiveUserWithId(testId);
        testUserEntity = UserTestUtils.createActiveUserEntityWithId(testId);
        testUserEntities = Arrays.asList(testUserEntity);
    }

    @Test
    void getAllUsers_ShouldReturnListOfActiveUsers() {
        when(userRepository.findByStatus(Status.ACTIVE)).thenReturn(testUserEntities);
        when(userAssembler.toModel(testUserEntity)).thenReturn(testUser);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).findByStatus(Status.ACTIVE);
        verify(userAssembler).toModel(testUserEntity);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testUserEntity));
        when(userAssembler.toModel(testUserEntity)).thenReturn(testUser);

        User result = userService.getUserById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userAssembler).toModel(testUserEntity);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(testId));
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userAssembler, never()).toModel(any());
    }

    @Test
    void createUser_ShouldValidateAndCreateUser() {
        // Given
        when(userAssembler.toEntity(testUser)).thenReturn(testUserEntity);
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userAssembler.toModel(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.createUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userValidationService).validateUserCreation(testUser);
        verify(userAssembler).toEntity(testUser);
        verify(userRepository).save(testUserEntity);
        verify(userAssembler).toModel(testUserEntity);
    }

    @Test
    void updateUser_WhenUserExists_ShouldValidateAndUpdateUser() {
        // Given
        User updateUser = UserTestUtils.createUserForUpdate();

        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userAssembler.toModel(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.updateUser(testId, updateUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userValidationService).validateUserUpdate(updateUser, testUserEntity);
        verify(userAssembler).updateEntity(testUserEntity, updateUser);
        verify(userRepository).save(testUserEntity);
        verify(userAssembler).toModel(testUserEntity);
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        User updateUser = UserTestUtils.createUserForUpdate();
        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, 
                () -> userService.updateUser(testId, updateUser));
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userValidationService, never()).validateUserUpdate(any(), any());
    }

    @Test
    void patchUser_ShouldCallUpdateUser() {
        // Given
        User patchUser = UserTestUtils.createUserForUpdate();
        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userAssembler.toModel(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.patchUser(testId, patchUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userValidationService).validateUserUpdate(patchUser, testUserEntity);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldSoftDeleteUser() {
        // Given
        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(testId));

        // Then
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userRepository).save(testUserEntity);
        assertEquals(Status.DELETED, testUserEntity.getStatus());
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(testId));
        verify(userRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(userRepository, never()).save(any());
    }
}