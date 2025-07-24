package com.example.springsddexample.repository;

import com.example.springsddexample.model.enums.Status;
import com.example.springsddexample.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    List<UserEntity> findByStatus(Status status);
    
    Optional<UserEntity> findByIdAndStatus(UUID id, Status status);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.username = :username AND u.status = 'ACTIVE'")
    boolean existsByUsernameAndStatusActive(@Param("username") String username);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.email = :email AND u.status = 'ACTIVE'")
    boolean existsByEmailAndStatusActive(@Param("email") String email);
}