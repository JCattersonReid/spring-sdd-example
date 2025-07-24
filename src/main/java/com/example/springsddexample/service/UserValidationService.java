package com.example.springsddexample.service;

import com.example.springsddexample.exception.UserAlreadyExistsException;
import com.example.springsddexample.model.dto.User;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    public void validateUserCreation(User user) {
        validateUsernameUniqueness(user.getUsername(), null);
        validateEmailUniqueness(user.getEmail(), null);
    }

    public void validateUserUpdate(User user, UserEntity existingUser) {
        if (user.getUsername() != null && !existingUser.getUsername().equals(user.getUsername())) {
            validateUsernameUniqueness(user.getUsername(), existingUser.getId());
        }
        if (user.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
            validateEmailUniqueness(user.getEmail(), existingUser.getId());
        }
    }

    private void validateUsernameUniqueness(String username, java.util.UUID excludeId) {
        if (username != null && userRepository.existsByUsernameAndStatusActive(username)) {
            throw new UserAlreadyExistsException("Username", username);
        }
    }

    private void validateEmailUniqueness(String email, java.util.UUID excludeId) {
        if (email != null && userRepository.existsByEmailAndStatusActive(email)) {
            throw new UserAlreadyExistsException("Email", email);
        }
    }
}