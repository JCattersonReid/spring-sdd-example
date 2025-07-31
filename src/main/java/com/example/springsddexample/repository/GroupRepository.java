package com.example.springsddexample.repository;

import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {

    List<GroupEntity> findByStatus(Status status);
    
    Optional<GroupEntity> findByIdAndStatus(UUID id, Status status);
    
    List<GroupEntity> findByStatusAndNameContaining(Status status, String name);
    
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GroupEntity g WHERE g.name = :name AND g.status = 'ACTIVE'")
    boolean existsByNameAndStatusActive(@Param("name") String name);
}