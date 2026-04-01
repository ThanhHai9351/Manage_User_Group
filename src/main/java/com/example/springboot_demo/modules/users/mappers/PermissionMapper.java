package com.example.springboot_demo.modules.users.mappers;

import org.mapstruct.Mapper;
import com.example.springboot_demo.mappers.BaseMapper;
import com.example.springboot_demo.modules.users.entities.Permission;
import com.example.springboot_demo.modules.users.resources.PermissionResource;
import com.example.springboot_demo.modules.users.request.Permission.StoreRequest;
import com.example.springboot_demo.modules.users.request.Permission.UpdateRequest;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends BaseMapper<Permission, PermissionResource, StoreRequest, UpdateRequest> {

}
