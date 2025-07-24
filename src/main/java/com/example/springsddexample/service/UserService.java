package com.example.springsddexample.service;

import com.example.springsddexample.exception.UserAlreadyExistsException;
import com.example.springsddexample.exception.UserNotFoundException;
import com.example.springsddexample.model.assembler.UserAssembler;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.dto.User;
import com.example.springsddexample.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAssembler userAssembler;

    public List<User> getAllUsers() {
        return userRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(userAssembler::toModel)
                .collect(Collectors.toList());
    }

    public User getUserById(UUID id) {
        return userRepository.findByIdAndStatus(id, Status.ACTIVE)
                .map(userAssembler::toModel)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(User user) {
        if (userRepository.existsByUsernameAndStatusActive(user.getUsername())) {
            throw new UserAlreadyExistsException("Username", user.getUsername());
        }
        if (userRepository.existsByEmailAndStatusActive(user.getEmail())) {
            throw new UserAlreadyExistsException("Email", user.getEmail());
        }

        UserEntity entity = userAssembler.toEntity(user);

        return userAssembler.toModel(userRepository.save(entity));
    }

    public User updateUser(UUID id, User user) {
        UserEntity existingUser = userRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        if (user.getUsername() != null && 
            !existingUser.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsernameAndStatusActive(user.getUsername())) {
            throw new UserAlreadyExistsException("Username", user.getUsername());
        }
        if (user.getEmail() != null && 
            !existingUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmailAndStatusActive(user.getEmail())) {
            throw new UserAlreadyExistsException("Email", user.getEmail());
        }
        
        userAssembler.updateEntity(existingUser, user);
        UserEntity savedEntity = userRepository.save(existingUser);
        return userAssembler.toModel(savedEntity);
    }

    public User patchUser(UUID id, User user) {
        return updateUser(id, user);
    }

    public void deleteUser(UUID id) {
        UserEntity userEntity = userRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        userEntity.setStatus(Status.DELETED);
        userRepository.save(userEntity);
    }

}