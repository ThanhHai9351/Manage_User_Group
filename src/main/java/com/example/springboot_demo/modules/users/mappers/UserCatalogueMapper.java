package com.example.springboot_demo.modules.users.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.springboot_demo.mappers.BaseMapper;
import com.example.springboot_demo.modules.users.entities.UserCatalogue;
import com.example.springboot_demo.modules.users.resources.UserCatalogueResource;
import com.example.springboot_demo.modules.users.request.UserCatalouge.StoreRequest;
import com.example.springboot_demo.modules.users.request.UserCatalouge.UpdateRequest;

import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.example.springboot_demo.annotations.BaseMapperAnnotation;

@Mapper(componentModel = "spring")
public interface UserCatalogueMapper
        extends BaseMapper<UserCatalogue, UserCatalogueResource, StoreRequest, UpdateRequest> {

    @Override
    default UserCatalogue toEntity(StoreRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        return UserCatalogue.builder()
                .name(createRequest.getName())
                .publish(createRequest.getPublish())
                .build();
    }

    @Override
    default void updateEntityFromRequest(UpdateRequest updateRequest,
            @MappingTarget UserCatalogue entity) {
        if (updateRequest == null) {
            return;
        }
        entity.setName(updateRequest.getName() != null ? updateRequest.getName() : entity.getName());
        entity.setPublish(updateRequest.getPublish() != null ? updateRequest.getPublish() : entity.getPublish());
    }

    // @Override
    // @BaseMapperAnnotation
    // @Mapping(target = "permissions", ignore = true)
    // @BeanMapping(nullValuePropertyMappingStrategy =
    // NullValuePropertyMappingStrategy.IGNORE)
    // UserCatalogue toEntity(StoreRequest createRequest);

    // @Override
    // @BaseMapperAnnotation
    // @Mapping(target = "permissions", ignore = true)
    // @BeanMapping(nullValuePropertyMappingStrategy =
    // NullValuePropertyMappingStrategy.IGNORE)
    // void updateEntityFromRequest(UpdateRequest updateRequest, @MappingTarget
    // UserCatalogue entity);

}
