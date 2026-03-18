package com.example.springboot_demo.modules.users.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.springboot_demo.modules.users.entities.User;
import com.example.springboot_demo.modules.users.entities.UserCatalogue;
import com.example.springboot_demo.modules.users.reponsitories.UserCatalogueRepository;
import com.example.springboot_demo.modules.users.reponsitories.UserRepository;
import com.example.springboot_demo.modules.users.resources.UserRescource;
import com.example.springboot_demo.resources.ErrorResource;

import jakarta.validation.Valid;

import com.example.springboot_demo.resources.ApiResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.springboot_demo.modules.users.request.UserCatalouge.StoreRequest;
import com.example.springboot_demo.modules.users.request.UserCatalouge.UpdateRequest;
import com.example.springboot_demo.modules.users.resources.UserCatalogueResource;
import com.example.springboot_demo.modules.users.service.interfaces.UserCatalogueServiceInterface;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import com.example.springboot_demo.modules.users.mappers.UserCatalogueMapper;

@RestController
@RequestMapping("/v1/user_catalogues")
public class UserCatalogueController {

    private static final Logger logger = LoggerFactory.getLogger(UserCatalogueController.class);

    private final UserCatalogueServiceInterface userCatalogueService;

    @Autowired
    private UserCatalogueRepository userCatalogueRepository;

    private final UserCatalogueMapper userCatalogueMapper;

    public UserCatalogueController(UserCatalogueServiceInterface userCatalogueService,
            UserCatalogueMapper userCatalogueMapper) {
        this.userCatalogueService = userCatalogueService;
        this.userCatalogueMapper = userCatalogueMapper;
    }

    @PostMapping(value = { "", "/" })
    public ResponseEntity<?> create(@Valid @RequestBody StoreRequest request) {
        try {
            UserCatalogue userCatalogue = userCatalogueService.create(request);

            // UserCatalogueResource userCatalogueRescource =
            // UserCatalogueResource.builder()
            // .id(userCatalogue.getId())
            // .name(userCatalogue.getName())
            // .publish(userCatalogue.getPublish())
            // .build();

            UserCatalogueResource userCatalogueRescource = userCatalogueMapper.tResource(userCatalogue);

            ApiResource<UserCatalogueResource> apiResource = ApiResource.ok(userCatalogueRescource,
                    "User catalogue created successfully");

            return ResponseEntity.ok(apiResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.error("FAILED_TO_CREATE_USER_CATALOGUE",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest request) {
        try {

            UserCatalogue userCatalogue = userCatalogueService.update(id, request);

            // UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
            // .id(userCatalogue.getId())
            // .name(userCatalogue.getName())
            // .publish(userCatalogue.getPublish())
            // .build();

            UserCatalogueResource userCatalogueResource = userCatalogueMapper.tResource(userCatalogue);

            ApiResource<UserCatalogueResource> apiResource = ApiResource.ok(userCatalogueResource,
                    "User catalogue updated successfully");

            return ResponseEntity.ok(apiResource);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResource.error("USER_CATALOGUE_NOT_FOUND",
                    e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.error("FAILED_TO_UPDATE_USER_CATALOGUE",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping(value = { "", "/" })
    public ResponseEntity<?> pagination(HttpServletRequest request) {
        try {

            Map<String, String[]> params = request.getParameterMap();

            Page<UserCatalogue> userCatalogues = userCatalogueService.pagination(params);

            // Page<UserCatalogueResource> userCatalogueResources = userCatalogues
            // .map(userCatalogueService -> UserCatalogueResource.builder()
            // .id(userCatalogueService.getId())
            // .name(userCatalogueService.getName())
            // .publish(userCatalogueService.getPublish())
            // .build());

            Page<UserCatalogueResource> userCatalogueResources = userCatalogueMapper.toResourcePage(userCatalogues);

            ApiResource<Page<UserCatalogueResource>> apiResource = ApiResource.ok(userCatalogueResources,
                    "User catalogues fetched successfully");

            return ResponseEntity.ok(apiResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.error("FAILED_TO_GET_ALL_USER_CATALOGUES",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(HttpServletRequest request) {
        try {
            Map<String, String[]> params = request.getParameterMap();
            List<UserCatalogue> userCatalogues = userCatalogueService.getAll(params);

            // List<UserCatalogueResource> userCatalogueResources =
            // userCatalogues.stream().map(
            // userCatalogue -> UserCatalogueResource.builder()
            // .id(userCatalogue.getId())
            // .name(userCatalogue.getName())
            // .publish(userCatalogue.getPublish())
            // .build())
            // .collect(Collectors.toList());

            List<UserCatalogueResource> userCatalogueResources = userCatalogueMapper.toList(userCatalogues);

            ApiResource<List<UserCatalogueResource>> apiResource = ApiResource.ok(userCatalogueResources,
                    "User catalogues fetched successfully");

            return ResponseEntity.ok(apiResource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.error("FAILED_TO_GET_ALL_USER_CATALOGUES",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));
            UserCatalogueResource userCatalogueResource = userCatalogueMapper.tResource(userCatalogue);
            ApiResource<UserCatalogueResource> apiResource = ApiResource.ok(userCatalogueResource,
                    "User catalogue fetched successfully");
            return ResponseEntity.ok(apiResource);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResource.error("USER_CATALOGUE_NOT_FOUND",
                    e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userCatalogueService.delete(id);
            return ResponseEntity.ok(ApiResource.message("User catalogue deleted successfully", HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResource.error("USER_CATALOGUE_NOT_FOUND",
                    e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.error("FAILED_TO_DELETE_USER_CATALOGUE",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping(value = { "", "/" })
    public ResponseEntity<?> deleteMany(@RequestBody List<Long> ids) {
        try {
            userCatalogueService.deleteMutipleEntity(ids);
            return ResponseEntity.ok(ApiResource.message("All user catalogues deleted successfully", HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResource.error("NO_USER_CATALOGUES_FOUND",
                    e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.error("FAILED_TO_DELETE_ALL_USER_CATALOGUES",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}