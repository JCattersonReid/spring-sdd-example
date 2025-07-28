package com.example.springsddexample.repository;

import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.util.GroupTestUtils;
import com.example.springsddexample.util.UserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GroupRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupRepository groupRepository;

    private UserEntity testAdmin;
    private GroupEntity activeGroup;
    private GroupEntity deletedGroup;

    @BeforeEach
    void setUp() {
        // Create and persist admin user
        testAdmin = UserTestUtils.createActiveUserEntity();
        entityManager.persistAndFlush(testAdmin);

        // Create and persist active group
        activeGroup = GroupTestUtils.createActiveGroupEntity();
        activeGroup.setAdmin(testAdmin);
        entityManager.persistAndFlush(activeGroup);

        // Create and persist deleted group
        deletedGroup = GroupTestUtils.createGroupEntityWithCustomName("Deleted Group");
        deletedGroup.setAdmin(testAdmin);
        deletedGroup.setStatus(Status.DELETED);
        entityManager.persistAndFlush(deletedGroup);

        entityManager.clear();
    }

    @Test
    void findByStatus_WhenStatusActive_ShouldReturnActiveGroups() {
        // When
        List<GroupEntity> result = groupRepository.findByStatus(Status.ACTIVE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activeGroup.getId(), result.get(0).getId());
        assertEquals(Status.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void findByStatus_WhenStatusDeleted_ShouldReturnDeletedGroups() {
        // When
        List<GroupEntity> result = groupRepository.findByStatus(Status.DELETED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(deletedGroup.getId(), result.get(0).getId());
        assertEquals(Status.DELETED, result.get(0).getStatus());
    }

    @Test
    void findByIdAndStatus_WhenGroupExistsAndActive_ShouldReturnGroup() {
        // When
        Optional<GroupEntity> result = groupRepository.findByIdAndStatus(activeGroup.getId(), Status.ACTIVE);

        // Then
        assertTrue(result.isPresent());
        assertEquals(activeGroup.getId(), result.get().getId());
        assertEquals(Status.ACTIVE, result.get().getStatus());
    }

    @Test
    void findByIdAndStatus_WhenGroupExistsButDeleted_ShouldReturnEmpty() {
        // When
        Optional<GroupEntity> result = groupRepository.findByIdAndStatus(deletedGroup.getId(), Status.ACTIVE);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByIdAndStatus_WhenGroupNotExists_ShouldReturnEmpty() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<GroupEntity> result = groupRepository.findByIdAndStatus(nonExistentId, Status.ACTIVE);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByStatusAndNameContaining_WhenNameMatches_ShouldReturnMatchingGroups() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "Test";

        // When
        Page<GroupEntity> result = groupRepository.findByStatusAndNameContaining(Status.ACTIVE, searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(activeGroup.getId(), result.getContent().get(0).getId());
    }

    @Test
    void findByStatusAndNameContaining_WhenNameDoesNotMatch_ShouldReturnEmpty() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "NonExistent";

        // When
        Page<GroupEntity> result = groupRepository.findByStatusAndNameContaining(Status.ACTIVE, searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void existsByNameAndStatusActive_WhenNameExistsAndActive_ShouldReturnTrue() {
        // When
        boolean result = groupRepository.existsByNameAndStatusActive(activeGroup.getName());

        // Then
        assertTrue(result);
    }

    @Test
    void existsByNameAndStatusActive_WhenNameExistsButDeleted_ShouldReturnFalse() {
        // When
        boolean result = groupRepository.existsByNameAndStatusActive(deletedGroup.getName());

        // Then
        assertFalse(result);
    }

    @Test
    void existsByNameAndStatusActive_WhenNameDoesNotExist_ShouldReturnFalse() {
        // When
        boolean result = groupRepository.existsByNameAndStatusActive("NonExistent Group");

        // Then
        assertFalse(result);
    }

    @Test
    void existsByNameAndStatusActiveAndIdNot_WhenNameExistsWithDifferentId_ShouldReturnTrue() {
        // Given
        UUID differentId = UUID.randomUUID();

        // When
        boolean result = groupRepository.existsByNameAndStatusActiveAndIdNot(activeGroup.getName(), differentId);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByNameAndStatusActiveAndIdNot_WhenNameExistsWithSameId_ShouldReturnFalse() {
        // When
        boolean result = groupRepository.existsByNameAndStatusActiveAndIdNot(activeGroup.getName(), activeGroup.getId());

        // Then
        assertFalse(result);
    }

    @Test
    void existsByNameAndStatusActiveAndIdNot_WhenNameDoesNotExist_ShouldReturnFalse() {
        // Given
        UUID someId = UUID.randomUUID();

        // When
        boolean result = groupRepository.existsByNameAndStatusActiveAndIdNot("NonExistent Group", someId);

        // Then
        assertFalse(result);
    }
}