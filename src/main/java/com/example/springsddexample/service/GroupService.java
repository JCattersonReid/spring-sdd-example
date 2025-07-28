package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupNotFoundException;
import com.example.springsddexample.model.assembler.GroupAssembler;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupAssembler groupAssembler;
    private final GroupValidationService groupValidationService;

    public List<Group> getAllGroups() {
        return groupRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(groupAssembler::toModel)
                .collect(Collectors.toList());
    }

    public Page<Group> searchGroups(String name, Pageable pageable) {
        Page<GroupEntity> entities = groupRepository.findByStatusAndNameContaining(Status.ACTIVE, name != null ? name : "", pageable);
        return entities.map(groupAssembler::toModel);
    }

    public Group getGroupById(UUID id) {
        return groupRepository.findByIdAndStatus(id, Status.ACTIVE)
                .map(groupAssembler::toModel)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    public Group createGroup(Group group) {
        groupValidationService.validateGroupCreation(group);

        GroupEntity entity = groupAssembler.toEntity(group);
        return groupAssembler.toModel(groupRepository.save(entity));
    }

    public Group updateGroup(UUID id, Group group) {
        GroupEntity existingGroup = groupRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new GroupNotFoundException(id));
        
        groupValidationService.validateGroupUpdate(group, existingGroup);
        
        groupAssembler.updateEntity(existingGroup, group);
        GroupEntity savedEntity = groupRepository.save(existingGroup);
        return groupAssembler.toModel(savedEntity);
    }

    public Group patchGroup(UUID id, Group group) {
        return updateGroup(id, group);
    }

    public void deleteGroup(UUID id) {
        GroupEntity groupEntity = groupRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new GroupNotFoundException(id));
        
        groupEntity.setStatus(Status.DELETED);
        groupRepository.save(groupEntity);
    }

}