package com.example.springboot_demo.modules.users.service.interfaces;

import com.example.springboot_demo.modules.users.request.Permission.StoreRequest;
import com.example.springboot_demo.modules.users.request.Permission.UpdateRequest;

import com.example.springboot_demo.modules.users.entities.Permission;

public interface PermissionServiceInterface
                extends BaseServiceInterface<Permission, StoreRequest, UpdateRequest> {

}
