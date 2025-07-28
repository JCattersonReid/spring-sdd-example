package com.example.springsddexample.model.dto;

import lombok.*;
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