package com.example.springsddexample.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GroupEntity extends CommonEntity {

    @Column(name = "name", nullable = false, length = 240)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "self_join", nullable = false)
    @Builder.Default
    private Boolean selfJoin = false;

    @Column(name = "self_leave", nullable = false)
    @Builder.Default
    private Boolean selfLeave = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private UserEntity admin;
}