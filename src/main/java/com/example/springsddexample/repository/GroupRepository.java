package com.example.springsddexample.repository;

import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.model.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    Page<GroupEntity> findByStatusAndNameContaining(Status status, String name, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GroupEntity g WHERE g.name = :name AND g.status = 'ACTIVE'")
    boolean existsByNameAndStatusActive(@Param("name") String name);
    
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GroupEntity g WHERE g.name = :name AND g.status = 'ACTIVE' AND g.id != :id")
    boolean existsByNameAndStatusActiveAndIdNot(@Param("name") String name, @Param("id") UUID id);
}