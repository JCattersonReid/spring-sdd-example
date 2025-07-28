package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupAlreadyExistsException;
import com.example.springsddexample.exception.UserNotFoundException;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.repository.GroupRepository;
import com.example.springsddexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupValidationService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public void validateGroupCreation(Group group) {
        validateGroupNameUniqueness(group.getName(), null);
        validateAdminExists(group.getAdmin().getId());
    }

    public void validateGroupUpdate(Group group, GroupEntity existingGroup) {
        if (group.getName() != null && !existingGroup.getName().equals(group.getName())) {
            validateGroupNameUniqueness(group.getName(), existingGroup.getId());
        }
        if (group.getAdmin() != null && !existingGroup.getAdmin().getId().equals(group.getAdmin().getId())) {
            validateAdminExists(group.getAdmin().getId());
        }
    }

    private void validateGroupNameUniqueness(String name, java.util.UUID excludeId) {
        if (name != null) {
            boolean exists = excludeId == null 
                ? groupRepository.existsByNameAndStatusActive(name)
                : groupRepository.existsByNameAndStatusActiveAndIdNot(name, excludeId);
            
            if (exists) {
                throw new GroupAlreadyExistsException("Group name", name);
            }
        }
    }

    private void validateAdminExists(java.util.UUID adminId) {
        if (adminId != null && !userRepository.findByIdAndStatus(adminId, Status.ACTIVE).isPresent()) {
            throw new UserNotFoundException(adminId);
        }
    }
}