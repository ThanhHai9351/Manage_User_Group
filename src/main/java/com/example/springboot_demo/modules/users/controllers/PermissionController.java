package com.example.springboot_demo.modules.users.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot_demo.modules.users.request.Permission.StoreRequest;
import com.example.springboot_demo.modules.users.request.Permission.UpdateRequest;
import com.example.springboot_demo.modules.users.resources.PermissionResource;
import com.example.springboot_demo.modules.users.service.interfaces.PermissionServiceInterface;

import com.example.springboot_demo.controllers.BaseController;
import com.example.springboot_demo.modules.users.mappers.PermissionMapper;
import com.example.springboot_demo.modules.users.reponsitories.PermissionRepository;
import com.example.springboot_demo.modules.users.entities.Permission;

@RestController
@RequestMapping("/v1/permissions")
public class PermissionController
        extends
        BaseController<Permission, PermissionResource, StoreRequest, UpdateRequest, PermissionRepository> {

    public PermissionController(PermissionServiceInterface permissionService,
            PermissionMapper permissionMapper, PermissionRepository permissionRepository) {
        super(permissionService, permissionMapper, permissionRepository);
    }
}