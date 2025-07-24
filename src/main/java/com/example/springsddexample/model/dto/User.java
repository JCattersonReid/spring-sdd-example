package com.example.springsddexample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends RepresentationModel<User> {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}