package com.example.springsddexample.service;

import com.example.springsddexample.exception.GroupNotFoundException;
import com.example.springsddexample.model.assembler.GroupAssembler;
import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.repository.GroupRepository;
import com.example.springsddexample.util.GroupTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private GroupAssembler groupAssembler;

    @Mock
    private GroupValidationService groupValidationService;

    @InjectMocks
    private GroupService groupService;

    private UUID testId;
    private Group testGroup;
    private GroupEntity testGroupEntity;
    private List<GroupEntity> testGroupEntities;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testGroup = GroupTestUtils.createActiveGroupWithId(testId);
        testGroupEntity = GroupTestUtils.createActiveGroupEntityWithId(testId);
        testGroupEntities = Arrays.asList(testGroupEntity);
    }

    @Test
    void getAllGroups_ShouldReturnListOfActiveGroups() {
        // Given
        when(groupRepository.findByStatus(Status.ACTIVE)).thenReturn(testGroupEntities);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        List<Group> result = groupService.getAllGroups();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testGroup, result.get(0));
        verify(groupRepository).findByStatus(Status.ACTIVE);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void searchGroups_WithNameFilter_ShouldReturnFilteredGroups() {
        // Given
        String searchName = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<GroupEntity> entityPage = new PageImpl<>(testGroupEntities, pageable, 1);
        
        when(groupRepository.findByStatusAndNameContaining(Status.ACTIVE, searchName, pageable))
                .thenReturn(entityPage);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        Page<Group> result = groupService.searchGroups(searchName, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testGroup, result.getContent().get(0));
        verify(groupRepository).findByStatusAndNameContaining(Status.ACTIVE, searchName, pageable);
    }

    @Test
    void searchGroups_WithNullName_ShouldReturnAllActiveGroups() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<GroupEntity> entityPage = new PageImpl<>(testGroupEntities, pageable, 1);
        
        when(groupRepository.findByStatusAndNameContaining(Status.ACTIVE, "", pageable))
                .thenReturn(entityPage);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        Page<Group> result = groupService.searchGroups(null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository).findByStatusAndNameContaining(Status.ACTIVE, "", pageable);
    }

    @Test
    void getGroupById_WhenGroupExists_ShouldReturnGroup() {
        // Given
        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        Group result = groupService.getGroupById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void getGroupById_WhenGroupNotExists_ShouldThrowGroupNotFoundException() {
        // Given
        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(testId));
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupAssembler, never()).toModel(any());
    }

    @Test
    void createGroup_ShouldValidateAndCreateGroup() {
        // Given
        Group groupForCreation = GroupTestUtils.createGroupForCreation();
        when(groupAssembler.toEntity(groupForCreation)).thenReturn(testGroupEntity);
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        Group result = groupService.createGroup(groupForCreation);

        // Then
        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(groupValidationService).validateGroupCreation(groupForCreation);
        verify(groupAssembler).toEntity(groupForCreation);
        verify(groupRepository).save(testGroupEntity);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void updateGroup_WhenGroupExists_ShouldValidateAndUpdateGroup() {
        // Given
        Group updateGroup = GroupTestUtils.createGroupForUpdate();

        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        Group result = groupService.updateGroup(testId, updateGroup);

        // Then
        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupValidationService).validateGroupUpdate(updateGroup, testGroupEntity);
        verify(groupAssembler).updateEntity(testGroupEntity, updateGroup);
        verify(groupRepository).save(testGroupEntity);
        verify(groupAssembler).toModel(testGroupEntity);
    }

    @Test
    void updateGroup_WhenGroupNotExists_ShouldThrowGroupNotFoundException() {
        // Given
        Group updateGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(GroupNotFoundException.class, 
                () -> groupService.updateGroup(testId, updateGroup));
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupValidationService, never()).validateGroupUpdate(any(), any());
    }

    @Test
    void patchGroup_ShouldCallUpdateGroup() {
        // Given
        Group patchGroup = GroupTestUtils.createGroupForUpdate();
        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);
        when(groupAssembler.toModel(testGroupEntity)).thenReturn(testGroup);

        // When
        Group result = groupService.patchGroup(testId, patchGroup);

        // Then
        assertNotNull(result);
        assertEquals(testGroup, result);
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupValidationService).validateGroupUpdate(patchGroup, testGroupEntity);
    }

    @Test
    void deleteGroup_WhenGroupExists_ShouldSoftDeleteGroup() {
        // Given
        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.of(testGroupEntity));
        when(groupRepository.save(testGroupEntity)).thenReturn(testGroupEntity);

        // When
        assertDoesNotThrow(() -> groupService.deleteGroup(testId));

        // Then
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupRepository).save(testGroupEntity);
        assertEquals(Status.DELETED, testGroupEntity.getStatus());
    }

    @Test
    void deleteGroup_WhenGroupNotExists_ShouldThrowGroupNotFoundException() {
        // Given
        when(groupRepository.findByIdAndStatus(testId, Status.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(testId));
        verify(groupRepository).findByIdAndStatus(testId, Status.ACTIVE);
        verify(groupRepository, never()).save(any());
    }
}