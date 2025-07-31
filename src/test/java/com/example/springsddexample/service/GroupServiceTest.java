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
import com.example.springsddexample.util.GroupTestUtils;
import com.example.springsddexample.util.UserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupAssembler groupAssembler;

    @Mock
    private GroupValidationService groupValidationService;

    @InjectMocks
    private GroupService groupService;

    private UUID testGroupId;
    private UUID testAdminId;
    private Group testGroup;
    private GroupEntity testGroupEntity;
    private UserEntity testAdminEntity;
    private List<GroupEntity> testGroupEntities;

    @BeforeEach
    void setUp() {
        testGroupId = UUID.randomUUID();
        testAdminId = UUID.randomUUID();
        testGroup = GroupTestUtils.createActiveGroupWithId(testGroupId, testAdminId);
        testAdminEntity = UserTestUtils.createActiveUserEntity(testAdminId);
        testGroupEntity = GroupTestUtils.createActiveGroupEntity(testGroupId, testAdminEntity);
        testGroupEntities = Arrays.asList(testGroupEntity);
    }

    @Test
    void shouldReturnAllActiveGroups() {
        when(groupRepository.findByStatus(Status.ACTIVE)).thenReturn(testGroupEntities);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        List<Group> result = groupService.getAllGroups();

        assertEquals(1, result.size());
        assertEquals(testGroup, result.get(0));
        verify(groupRepository).findByStatus(Status.ACTIVE);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void shouldReturnGroupByIdWhenExists() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.of(testGroupEntity));
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.getGroupById(testGroupId);

        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void shouldThrowGroupNotFoundExceptionWhenGroupDoesNotExist() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(testGroupId));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verifyNoInteractions(groupAssembler);
    }

    @Test
    void shouldReturnGroupsWhenSearchingByName() {
        String searchTerm = "Test";
        when(groupRepository.findByStatusAndNameContaining(Status.ACTIVE, searchTerm)).thenReturn(testGroupEntities);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        List<Group> result = groupService.searchGroups(searchTerm);

        assertEquals(1, result.size());
        assertEquals(testGroup, result.get(0));
        verify(groupRepository).findByStatusAndNameContaining(Status.ACTIVE, searchTerm);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void shouldCreateGroupWhenValidInput() {
        Group newGroup = GroupTestUtils.createGroupForCreation(testAdminId);
        GroupEntity newGroupEntity = GroupTestUtils.createActiveGroupEntity(null, testAdminEntity);
        
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE)).thenReturn(Optional.of(testAdminEntity));
        when(groupAssembler.toEntity(newGroup)).thenReturn(newGroupEntity);
        when(groupRepository.save(newGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.createGroup(newGroup);

        assertEquals(testGroup, result);
        verify(groupValidationService).validateGroupCreation(newGroup);
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
        verify(groupAssembler).toEntity(newGroup);
        verify(groupRepository).save(newGroupEntity);
        verify(groupAssembler).toModel(testGroupEntity);
        verify(newGroupEntity).setAdmin(testAdminEntity);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenAdminDoesNotExist() {
        Group newGroup = GroupTestUtils.createGroupForCreation(testAdminId);
        
        when(userRepository.findByIdAndStatus(testAdminId, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupService.createGroup(newGroup));
        verify(groupValidationService).validateGroupCreation(newGroup);
        verify(userRepository).findByIdAndStatus(testAdminId, Status.ACTIVE);
        verifyNoInteractions(groupAssembler);
        verifyNoInteractions(groupRepository);
    }

    @Test
    void shouldUpdateGroupWhenValidInput() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.updateGroup(testGroupId, updateGroup);

        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupValidationService).validateGroupUpdate(updateGroup, testGroupEntity);
        verify(groupAssembler).updateEntity(testGroupEntity, updateGroup);
        verify(groupRepository).save(testGroupEntity);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void shouldUpdateGroupAdminWhenAdminIdChanged() {
        UUID newAdminId = UUID.randomUUID();
        UserEntity newAdminEntity = UserTestUtils.createActiveUserEntity(newAdminId);
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        updateGroup.setAdminId(newAdminId);
        
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.of(testGroupEntity));
        when(userRepository.findByIdAndStatus(newAdminId, Status.ACTIVE)).thenReturn(Optional.of(newAdminEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.updateGroup(testGroupId, updateGroup);

        assertEquals(testGroup, result);
        verify(userRepository).findByIdAndStatus(newAdminId, Status.ACTIVE);
        verify(testGroupEntity).setAdmin(newAdminEntity);
    }

    @Test
    void shouldThrowGroupNotFoundExceptionWhenUpdatingNonExistentGroup() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.updateGroup(testGroupId, updateGroup));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verifyNoInteractions(groupValidationService);
        verifyNoInteractions(groupAssembler);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenNewAdminDoesNotExist() {
        UUID newAdminId = UUID.randomUUID();
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        updateGroup.setAdminId(newAdminId);
        
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.of(testGroupEntity));
        when(userRepository.findByIdAndStatus(newAdminId, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupService.updateGroup(testGroupId, updateGroup));
        verify(userRepository).findByIdAndStatus(newAdminId, Status.ACTIVE);
    }

    @Test
    void shouldSoftDeleteGroupWhenExists() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.of(testGroupEntity));

        groupService.deleteGroup(testGroupId);

        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(testGroupEntity).setStatus(Status.DELETED);
        verify(groupRepository).save(testGroupEntity);
    }

    @Test
    void shouldThrowGroupNotFoundExceptionWhenDeletingNonExistentGroup() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(testGroupId));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupRepository, never()).save(any());
    }
}