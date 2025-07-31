package com.example.springsddexample.model.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group extends RepresentationModel<Group> {

    private UUID id;
    private String name;
    private String description;
    private Boolean selfJoin;
    private Boolean selfLeave;
    private UUID adminId;
}