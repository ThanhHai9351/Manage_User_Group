package com.example.springboot_demo.modules.users.service.interfaces;

import com.example.springboot_demo.modules.users.request.UserCatalouge.StoreRequest;
import com.example.springboot_demo.modules.users.request.UserCatalouge.UpdateRequest;

import java.util.Map;
import java.util.List;

import org.springframework.data.domain.Page;

import com.example.springboot_demo.modules.users.entities.UserCatalogue;

public interface UserCatalogueServiceInterface {
    UserCatalogue create(StoreRequest request);

    UserCatalogue update(Long id, UpdateRequest request);

    Page<UserCatalogue> pagination(Map<String, String[]> params);

    List<UserCatalogue> getAll(Map<String, String[]> params);

}
