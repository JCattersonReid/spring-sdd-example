package com.example.springsddexample.service;

import com.example.springsddexample.exception.UserAlreadyExistsException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidationService userValidationService;

    private UUID testId;
    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testUser = UserTestUtils.createActiveUserWithId(testId);
        testUserEntity = UserTestUtils.createUserEntityWithCustomUsername("existinguser")
                .toBuilder()
                .id(testId)
                .email("existing@example.com")
                .build();
    }

    @Test
    void validateUserCreation_WhenUsernameAndEmailAreUnique_ShouldNotThrowException() {
        when(userRepository.existsByUsernameAndStatusActive("testuser")).thenReturn(false);
        when(userRepository.existsByEmailAndStatusActive("test@example.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidationService.validateUserCreation(testUser));
        verify(userRepository).existsByUsernameAndStatusActive("testuser");
        verify(userRepository).existsByEmailAndStatusActive("test@example.com");
    }

    @Test
    void validateUserCreation_WhenUsernameExists_ShouldThrowUserAlreadyExistsException() {
        when(userRepository.existsByUsernameAndStatusActive("testuser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userValidationService.validateUserCreation(testUser));
        assertEquals("Username already exists: testuser", exception.getMessage());
        verify(userRepository).existsByUsernameAndStatusActive("testuser");
        verify(userRepository, never()).existsByEmailAndStatusActive(anyString());
    }

    @Test
    void validateUserCreation_WhenEmailExists_ShouldThrowUserAlreadyExistsException() {
        when(userRepository.existsByUsernameAndStatusActive("testuser")).thenReturn(false);
        when(userRepository.existsByEmailAndStatusActive("test@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userValidationService.validateUserCreation(testUser));
        assertEquals("Email already exists: test@example.com", exception.getMessage());
        verify(userRepository).existsByUsernameAndStatusActive("testuser");
        verify(userRepository).existsByEmailAndStatusActive("test@example.com");
    }

    @Test
    void validateUserUpdate_WhenNoFieldsChanged_ShouldNotValidateUniqueness() {
        User updateUser = User.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(updateUser, testUserEntity));

        verify(userRepository, never()).existsByUsernameAndStatusActive(anyString());
        verify(userRepository, never()).existsByEmailAndStatusActive(anyString());
    }

    @Test
    void validateUserUpdate_WhenUsernameChangedToUnique_ShouldNotThrowException() {
        User updateUser = User.builder()
                .username("newusername")
                .build();
        when(userRepository.existsByUsernameAndStatusActive("newusername")).thenReturn(false);

        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        verify(userRepository).existsByUsernameAndStatusActive("newusername");
    }

    @Test
    void validateUserUpdate_WhenUsernameChangedToExisting_ShouldThrowUserAlreadyExistsException() {
        User updateUser = User.builder()
                .username("duplicateusername")
                .build();
        when(userRepository.existsByUsernameAndStatusActive("duplicateusername")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        assertEquals("Username already exists: duplicateusername", exception.getMessage());
        verify(userRepository).existsByUsernameAndStatusActive("duplicateusername");
    }

    @Test
    void validateUserUpdate_WhenEmailChangedToUnique_ShouldNotThrowException() {
        User updateUser = User.builder()
                .email("newemail@example.com")
                .build();
        when(userRepository.existsByEmailAndStatusActive("newemail@example.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        verify(userRepository).existsByEmailAndStatusActive("newemail@example.com");
    }

    @Test
    void validateUserUpdate_WhenEmailChangedToExisting_ShouldThrowUserAlreadyExistsException() {
        User updateUser = User.builder()
                .email("duplicate@example.com")
                .build();
        when(userRepository.existsByEmailAndStatusActive("duplicate@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        assertEquals("Email already exists: duplicate@example.com", exception.getMessage());
        verify(userRepository).existsByEmailAndStatusActive("duplicate@example.com");
    }

    @Test
    void validateUserUpdate_WhenUsernameUnchanged_ShouldNotValidateUsername() {
        User updateUser = User.builder()
                .username("existinguser")
                .email("newemail@example.com")
                .build();
        when(userRepository.existsByEmailAndStatusActive("newemail@example.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        verify(userRepository, never()).existsByUsernameAndStatusActive(anyString());
        verify(userRepository).existsByEmailAndStatusActive("newemail@example.com");
    }

    @Test
    void validateUserUpdate_WhenEmailUnchanged_ShouldNotValidateEmail() {
        User updateUser = User.builder()
                .username("newusername")
                .email("existing@example.com")
                .build();
        when(userRepository.existsByUsernameAndStatusActive("newusername")).thenReturn(false);

        assertDoesNotThrow(() -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        verify(userRepository).existsByUsernameAndStatusActive("newusername");
        verify(userRepository, never()).existsByEmailAndStatusActive(anyString());
    }

    @Test
    void validateUserUpdate_WhenBothFieldsChangedToExisting_ShouldThrowUsernameExceptionFirst() {
        User updateUser = User.builder()
                .username("duplicateusername")
                .email("duplicate@example.com")
                .build();
        when(userRepository.existsByUsernameAndStatusActive("duplicateusername")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userValidationService.validateUserUpdate(updateUser, testUserEntity));
        assertEquals("Username already exists: duplicateusername", exception.getMessage());
        verify(userRepository).existsByUsernameAndStatusActive("duplicateusername");
        verify(userRepository, never()).existsByEmailAndStatusActive(anyString());
    }
}