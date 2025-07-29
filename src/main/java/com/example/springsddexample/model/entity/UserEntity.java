package com.example.springsddexample.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserEntity extends CommonEntity {

    private String username;

    private String email;

    private String firstName;

    private String lastName;
}