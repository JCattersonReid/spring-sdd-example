package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupAlreadyExistsException;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupValidationService {

    private final GroupRepository groupRepository;

    public void validateGroupCreation(Group group) {
        validateNameUniqueness(group.getName());
    }

    public void validateGroupUpdate(Group group, GroupEntity existingGroup) {
        if (group.getName() != null && !existingGroup.getName().equals(group.getName())) {
            validateNameUniqueness(group.getName());
        }
    }

    private void validateNameUniqueness(String name) {
        if (name != null && groupRepository.existsByNameAndStatusActive(name)) {
            throw new GroupAlreadyExistsException(name);
        }
    }
}