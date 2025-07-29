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
        testAdmin = UserTestUtils.createActiveUserEntity(UUID.randomUUID());
        entityManager.persistAndFlush(testAdmin);

        activeGroup = GroupTestUtils.createGroupEntityWithCustomName("Active Group", testAdmin);
        entityManager.persistAndFlush(activeGroup);

        deletedGroup = GroupTestUtils.createGroupEntityWithCustomName("Deleted Group", testAdmin);
        deletedGroup.setStatus(Status.DELETED);
        entityManager.persistAndFlush(deletedGroup);

        entityManager.clear();
    }

    @Test
    void findByStatusShouldReturnOnlyActiveGroups() {
        List<GroupEntity> result = groupRepository.findByStatus(Status.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active Group", result.get(0).getName());
        assertEquals(Status.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void findByStatusShouldReturnOnlyDeletedGroups() {
        List<GroupEntity> result = groupRepository.findByStatus(Status.DELETED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Deleted Group", result.get(0).getName());
        assertEquals(Status.DELETED, result.get(0).getStatus());
    }

    @Test
    void findByIdAndStatusWhenActiveGroupExistsShouldReturnGroup() {
        Optional<GroupEntity> result = groupRepository.findByIdAndStatus(activeGroup.getId(), Status.ACTIVE);

        assertTrue(result.isPresent());
        assertEquals("Active Group", result.get().getName());
        assertEquals(Status.ACTIVE, result.get().getStatus());
    }

    @Test
    void findByIdAndStatusWhenDeletedGroupSearchedAsActiveShouldReturnEmpty() {
        Optional<GroupEntity> result = groupRepository.findByIdAndStatus(deletedGroup.getId(), Status.ACTIVE);

        assertFalse(result.isPresent());
    }

    @Test
    void findByStatusAndNameContainingShouldReturnMatchingActiveGroups() {
        GroupEntity partialMatchGroup = GroupTestUtils.createGroupEntityWithCustomName("Test Active Group", testAdmin);
        entityManager.persistAndFlush(partialMatchGroup);

        List<GroupEntity> result = groupRepository.findByStatusAndNameContaining(Status.ACTIVE, "Active");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(group -> group.getName().contains("Active")));
        assertTrue(result.stream().allMatch(group -> group.getStatus() == Status.ACTIVE));
    }

    @Test
    void findByStatusAndNameContainingShouldNotReturnDeletedGroups() {
        List<GroupEntity> result = groupRepository.findByStatusAndNameContaining(Status.ACTIVE, "Deleted");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByNameAndStatusActiveWhenActiveGroupExistsShouldReturnTrue() {
        boolean result = groupRepository.existsByNameAndStatusActive("Active Group");

        assertTrue(result);
    }

    @Test
    void existsByNameAndStatusActiveWhenDeletedGroupExistsShouldReturnFalse() {
        boolean result = groupRepository.existsByNameAndStatusActive("Deleted Group");

        assertFalse(result);
    }

    @Test
    void existsByNameAndStatusActiveWhenGroupNotExistsShouldReturnFalse() {
        boolean result = groupRepository.existsByNameAndStatusActive("Non-existent Group");

        assertFalse(result);
    }

    @Test
    void existsByNameAndStatusActiveAndIdNotWhenDifferentActiveGroupExistsShouldReturnTrue() {
        GroupEntity anotherGroup = GroupTestUtils.createGroupEntityWithCustomName("Another Active Group", testAdmin);
        entityManager.persistAndFlush(anotherGroup);

        boolean result = groupRepository.existsByNameAndStatusActiveAndIdNot("Another Active Group", activeGroup.getId());

        assertTrue(result);
    }

    @Test
    void existsByNameAndStatusActiveAndIdNotWhenSameGroupShouldReturnFalse() {
        boolean result = groupRepository.existsByNameAndStatusActiveAndIdNot("Active Group", activeGroup.getId());

        assertFalse(result);
    }

    @Test
    void existsByNameAndStatusActiveAndIdNotWhenGroupNotExistsShouldReturnFalse() {
        boolean result = groupRepository.existsByNameAndStatusActiveAndIdNot("Non-existent Group", activeGroup.getId());

        assertFalse(result);
    }
}