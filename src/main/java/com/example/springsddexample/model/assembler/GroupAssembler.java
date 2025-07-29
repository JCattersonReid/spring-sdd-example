package com.example.springsddexample.model.assembler;

import com.example.springsddexample.controller.GroupController;
import com.example.springsddexample.model.entity.GroupEntity;
import com.example.springsddexample.model.dto.Group;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class GroupAssembler extends RepresentationModelAssemblerSupport<GroupEntity, Group> {

    private final ModelMapper mapper;

    @Autowired
    public GroupAssembler(ModelMapper mapper) {
        super(GroupController.class, Group.class);
        this.mapper = mapper;
    }

    @Override
    public Group toModel(GroupEntity entity) {
        Group group = mapper.map(entity, Group.class);
        group.setAdminId(entity.getAdmin().getId());
        
        group.add(linkTo(methodOn(GroupController.class).getGroupById(entity.getId())).withSelfRel());
        group.add(linkTo(GroupController.class).withRel("groups"));
        
        return group;
    }

    public GroupEntity toEntity(Group group) {
        return mapper.map(group, GroupEntity.class);
    }

    public void updateEntity(GroupEntity entity, Group group) {
        entity.setName(group.getName());
        entity.setDescription(group.getDescription());
        entity.setSelfJoin(group.getSelfJoin());
        entity.setSelfLeave(group.getSelfLeave());
    }
}