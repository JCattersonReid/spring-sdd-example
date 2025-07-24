package com.example.springsddexample.model.assembler;

import com.example.springsddexample.controller.UserController;
import com.example.springsddexample.model.entity.UserEntity;
import com.example.springsddexample.model.dto.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserAssembler extends RepresentationModelAssemblerSupport<UserEntity, User> {

    private final ModelMapper mapper;

    @Autowired
    public UserAssembler(ModelMapper mapper) {
        super(UserController.class, User.class);
        this.mapper = mapper;
    }

    @Override
    public User toModel(UserEntity entity) {
        User user = mapper.map(entity, User.class);
        
        user.add(linkTo(methodOn(UserController.class).getUserById(entity.getId())).withSelfRel());
        user.add(linkTo(UserController.class).withRel("users"));
        
        return user;
    }

    public UserEntity toEntity(User user) {
        return mapper.map(user, UserEntity.class);
    }

    public void updateEntity(UserEntity entity, User user) {
        mapper.map(user, entity);
    }
}