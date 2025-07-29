package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupAlreadyExistsException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupAssembler groupAssembler;

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
    void getAllGroupsShouldReturnListOfActiveGroups() {
        when(groupRepository.findByStatus(Status.ACTIVE)).thenReturn(testGroupEntities);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        List<Group> result = groupService.getAllGroups();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testGroup, result.get(0));
        verify(groupRepository).findByStatus(Status.ACTIVE);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void getGroupByIdWhenGroupExistsShouldReturnGroup() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.getGroupById(testGroupId);

        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void getGroupByIdWhenGroupNotExistsShouldThrowGroupNotFoundException() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(testGroupId));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupAssembler, never()).toModel(any());
    }

    @Test
    void searchGroupsByNameShouldReturnMatchingGroups() {
        String searchName = "Test";
        when(groupRepository.findByStatusAndNameContaining(Status.ACTIVE, searchName))
                .thenReturn(testGroupEntities);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        List<Group> result = groupService.searchGroupsByName(searchName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testGroup, result.get(0));
        verify(groupRepository).findByStatusAndNameContaining(Status.ACTIVE, searchName);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void createGroupWhenValidShouldCreateGroup() {
        Group newGroup = GroupTestUtils.createActiveGroup();
        when(userRepository.findByIdAndStatus(newGroup.getAdminId(), Status.ACTIVE))
                .thenReturn(Optional.of(testAdminEntity));
        when(groupRepository.existsByNameAndStatusActive(newGroup.getName())).thenReturn(false);
        when(groupAssembler.toEntity(newGroup)).thenReturn(testGroupEntity);
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.createGroup(newGroup);

        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(userRepository).findByIdAndStatus(newGroup.getAdminId(), Status.ACTIVE);
        verify(groupRepository).existsByNameAndStatusActive(newGroup.getName());
        verify(groupAssembler).toEntity(newGroup);
        verify(testGroupEntity).setAdmin(testAdminEntity);
        verify(groupRepository).save(testGroupEntity);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void createGroupWhenNameIsNullShouldThrowIllegalArgumentException() {
        Group invalidGroup = GroupTestUtils.createActiveGroup();
        invalidGroup.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> groupService.createGroup(invalidGroup));
        assertEquals("Group name cannot be null or empty", exception.getMessage());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroupWhenNameIsEmptyShouldThrowIllegalArgumentException() {
        Group invalidGroup = GroupTestUtils.createActiveGroup();
        invalidGroup.setName("   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> groupService.createGroup(invalidGroup));
        assertEquals("Group name cannot be null or empty", exception.getMessage());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroupWhenDescriptionIsNullShouldThrowIllegalArgumentException() {
        Group invalidGroup = GroupTestUtils.createActiveGroup();
        invalidGroup.setDescription(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> groupService.createGroup(invalidGroup));
        assertEquals("Group description cannot be null or empty", exception.getMessage());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroupWhenDescriptionIsEmptyShouldThrowIllegalArgumentException() {
        Group invalidGroup = GroupTestUtils.createActiveGroup();
        invalidGroup.setDescription("   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> groupService.createGroup(invalidGroup));
        assertEquals("Group description cannot be null or empty", exception.getMessage());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroupWhenAdminIdIsNullShouldThrowIllegalArgumentException() {
        Group invalidGroup = GroupTestUtils.createActiveGroup();
        invalidGroup.setAdminId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> groupService.createGroup(invalidGroup));
        assertEquals("Admin ID cannot be null", exception.getMessage());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroupWhenNameAlreadyExistsShouldThrowGroupAlreadyExistsException() {
        Group newGroup = GroupTestUtils.createActiveGroup();
        when(groupRepository.existsByNameAndStatusActive(newGroup.getName())).thenReturn(true);

        assertThrows(GroupAlreadyExistsException.class, () -> groupService.createGroup(newGroup));
        verify(groupRepository).existsByNameAndStatusActive(newGroup.getName());
        verify(groupRepository, never()).save(any());
    }

    @Test
    void createGroupWhenAdminNotFoundShouldThrowUserNotFoundException() {
        Group newGroup = GroupTestUtils.createActiveGroup();
        when(groupRepository.existsByNameAndStatusActive(newGroup.getName())).thenReturn(false);
        when(userRepository.findByIdAndStatus(newGroup.getAdminId(), Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupService.createGroup(newGroup));
        verify(userRepository).findByIdAndStatus(newGroup.getAdminId(), Status.ACTIVE);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void updateGroupWhenGroupExistsShouldUpdateGroup() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId))
                .thenReturn(false);
        when(userRepository.findByIdAndStatus(updateGroup.getAdminId(), Status.ACTIVE))
                .thenReturn(Optional.of(testAdminEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.updateGroup(testGroupId, updateGroup);

        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupRepository).existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId);
        verify(userRepository).findByIdAndStatus(updateGroup.getAdminId(), Status.ACTIVE);
        verify(testGroupEntity).setAdmin(testAdminEntity);
        verify(groupAssembler).updateEntity(testGroupEntity, updateGroup);
        verify(groupRepository).save(testGroupEntity);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void updateGroupWhenGroupNotExistsShouldThrowGroupNotFoundException() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, 
                () -> groupService.updateGroup(testGroupId, updateGroup));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void updateGroupWhenNameAlreadyExistsShouldThrowGroupAlreadyExistsException() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId))
                .thenReturn(true);

        assertThrows(GroupAlreadyExistsException.class, 
                () -> groupService.updateGroup(testGroupId, updateGroup));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupRepository).existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void updateGroupWhenNewAdminNotFoundShouldThrowUserNotFoundException() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId))
                .thenReturn(false);
        when(userRepository.findByIdAndStatus(updateGroup.getAdminId(), Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, 
                () -> groupService.updateGroup(testGroupId, updateGroup));
        verify(userRepository).findByIdAndStatus(updateGroup.getAdminId(), Status.ACTIVE);
        verify(groupRepository, never()).save(any());
    }

    @Test
    void updateGroupWhenAdminIdIsNullShouldNotChangeAdmin() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        updateGroup.setAdminId(null);
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId))
                .thenReturn(false);
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.updateGroup(testGroupId, updateGroup);

        assertNotNull(result);
        verify(testGroupEntity, never()).setAdmin(any());
        verify(userRepository, never()).findByIdAndStatus(any(), eq(Status.ACTIVE));
    }

    @Test
    void updateGroupWhenAdminIdIsSameShouldNotChangeAdmin() {
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        updateGroup.setAdminId(testAdminId);
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.existsByNameAndStatusActiveAndIdNot(updateGroup.getName(), testGroupId))
                .thenReturn(false);
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        Group result = groupService.updateGroup(testGroupId, updateGroup);

        assertNotNull(result);
        verify(testGroupEntity, never()).setAdmin(any());
        verify(userRepository, never()).findByIdAndStatus(any(), eq(Status.ACTIVE));
    }

    @Test
    void deleteGroupWhenGroupExistsShouldSoftDeleteGroup() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);

        assertDoesNotThrow(() -> groupService.deleteGroup(testGroupId));

        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupRepository).save(testGroupEntity);
        assertEquals(Status.DELETED, testGroupEntity.getStatus());
    }

    @Test
    void deleteGroupWhenGroupNotExistsShouldThrowGroupNotFoundException() {
        when(groupRepository.findByIdAndStatus(testGroupId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(testGroupId));
        verify(groupRepository).findByIdAndStatus(testGroupId, Status.ACTIVE);
        verify(groupRepository, never()).save(any());
    }
}