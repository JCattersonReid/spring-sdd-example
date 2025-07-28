package com.example.springsddexample.controller;

import com.example.springsddexample.model.dto.Group;
import com.example.springsddexample.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Group>> searchGroups(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        return ResponseEntity.ok(groupService.searchGroups(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(group));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable UUID id, @RequestBody Group updatedGroup) {
        return ResponseEntity.ok(groupService.updateGroup(id, updatedGroup));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Group> patchGroup(@PathVariable UUID id, @RequestBody Group patch) {
        return ResponseEntity.ok(groupService.patchGroup(id, patch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}