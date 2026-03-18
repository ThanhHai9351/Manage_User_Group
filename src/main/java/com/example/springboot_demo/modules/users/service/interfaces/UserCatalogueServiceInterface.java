package com.example.springboot_demo.modules.users.service.interfaces;

import com.example.springboot_demo.modules.users.request.UserCatalouge.StoreRequest;
import com.example.springboot_demo.modules.users.request.UserCatalouge.UpdateRequest;

import com.example.springboot_demo.modules.users.entities.UserCatalogue;

public interface UserCatalogueServiceInterface
        extends BaseServiceInterface<UserCatalogue, StoreRequest, UpdateRequest> {

}
