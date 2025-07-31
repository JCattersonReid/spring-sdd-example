package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupNotFoundException;
import com.example.springsddexample.exception.UserNotFoundException;
import com.example.springsddexample.model.assembler.GroupAssembler;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.repository.GroupRepository;
import com.example.springsddexample.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupAssembler groupAssembler;
    private final GroupValidationService groupValidationService;

    public List<Group> getAllGroups() {
        return groupRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(groupAssembler::toModel)
                .collect(Collectors.toList());
    }

    public Group getGroupById(UUID id) {
        return groupRepository.findByIdAndStatus(id, Status.ACTIVE)
                .map(groupAssembler::toModel)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    public List<Group> searchGroups(String name) {
        return groupRepository.findByStatusAndNameContaining(Status.ACTIVE, name)
                .stream()
                .map(groupAssembler::toModel)
                .collect(Collectors.toList());
    }

    public Group createGroup(Group group) {
        groupValidationService.validateGroupCreation(group);

        UserEntity admin = userRepository.findByIdAndStatus(group.getAdminId(), Status.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException(group.getAdminId()));

        GroupEntity entity = groupAssembler.toEntity(group);
        entity.setAdmin(admin);
        
        return groupAssembler.toModel(groupRepository.save(entity));
    }

    public Group updateGroup(UUID id, Group group) {
        GroupEntity existingGroup = groupRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new GroupNotFoundException(id));
        
        groupValidationService.validateGroupUpdate(group, existingGroup);
        
        if (group.getAdminId() != null && !group.getAdminId().equals(existingGroup.getAdmin().getId())) {
            UserEntity newAdmin = userRepository.findByIdAndStatus(group.getAdminId(), Status.ACTIVE)
                    .orElseThrow(() -> new UserNotFoundException(group.getAdminId()));
            existingGroup.setAdmin(newAdmin);
        }
        
        groupAssembler.updateEntity(existingGroup, group);
        GroupEntity savedEntity = groupRepository.save(existingGroup);
        return groupAssembler.toModel(savedEntity);
    }

    public void deleteGroup(UUID id) {
        GroupEntity groupEntity = groupRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new GroupNotFoundException(id));
        
        groupEntity.setStatus(Status.DELETED);
        groupRepository.save(groupEntity);
    }
}