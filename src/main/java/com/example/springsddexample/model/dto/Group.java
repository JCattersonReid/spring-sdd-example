package com.example.springsddexample.model.dto;

import com.example.springsddexample.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.RepresentationModel;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Group extends RepresentationModel<Group> {

    private UUID id;
    private String name;
    private String description;
    private Boolean selfJoin;
    private Boolean selfLeave;
    private User admin;
    private Status status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}