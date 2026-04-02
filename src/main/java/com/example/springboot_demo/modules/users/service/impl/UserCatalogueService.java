package com.example.springboot_demo.modules.users.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot_demo.services.BaseService;
import com.example.springboot_demo.modules.users.service.interfaces.UserCatalogueServiceInterface;
import com.example.springboot_demo.modules.users.request.UserCatalouge.StoreRequest;
import com.example.springboot_demo.modules.users.request.UserCatalouge.UpdateRequest;
import com.example.springboot_demo.helpers.FilterParameter;
import com.example.springboot_demo.modules.users.entities.UserCatalogue;
import com.example.springboot_demo.modules.users.reponsitories.UserCatalogueRepository;
import com.example.springboot_demo.specifications.BaseSpecification;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.springboot_demo.modules.users.mappers.UserCatalogueMapper;

@Service
public class UserCatalogueService
        extends BaseService<UserCatalogue, UserCatalogueMapper, StoreRequest, UpdateRequest, UserCatalogueRepository>
        implements UserCatalogueServiceInterface {

    // private static final Logger logger =
    // LoggerFactory.getLogger(UserCatalogueService.class);

    @Autowired
    private UserCatalogueRepository userCatalogueRepository;

    private final UserCatalogueMapper userCatalogueMapper;

    @Override
    protected String[] getSearchFields() {
        return new String[] { "name" };
    }

    @Override
    protected UserCatalogueRepository getRepository() {
        return userCatalogueRepository;
    }

    @Override
    protected UserCatalogueMapper getMapper() {
        return userCatalogueMapper;
    }

    public UserCatalogueService(
            UserCatalogueMapper userCatalogueMapper) {
        this.userCatalogueMapper = userCatalogueMapper;
    }

    @Override
    protected String[] getRelations() {
        return new String[] { "permissions" };
    }

    // @Override
    // @Transactional
    // public UserCatalogue create(StoreRequest request) {
    // try {
    // // UserCatalogue userCatalogue = UserCatalogue.builder()
    // // .name(request.getName())
    // // .publish(request.getPublish())
    // // .build();
    // UserCatalogue userCatalogue = userCatalogueMapper.toEntity(request);
    // return userCatalogueRepository.save(userCatalogue);
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to create user catalogue: " +
    // e.getMessage());

    // }
    // }

    // @Override
    // @Transactional
    // public UserCatalogue update(Long id, UpdateRequest request) {
    // try {
    // UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));

    // // UserCatalogue updatedUserCatalogue = userCatalogue.builder()
    // // .id(userCatalogue.getId())
    // // .name(request.getName() != null ? request.getName() :
    // // userCatalogue.getName())
    // // .publish(request.getPublish() != null ? request.getPublish() :
    // // userCatalogue.getPublish())
    // // .updatedAt(LocalDateTime.now())
    // // .build();

    // userCatalogueMapper.updateEntityFromRequest(request, userCatalogue);

    // return userCatalogueRepository.save(userCatalogue);
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to update user catalogue: " +
    // e.getMessage());
    // }
    // }

    // @Override
    // public Page<UserCatalogue> pagination(Map<String, String[]> params) {
    // try {
    // int page = params.containsKey("page") ?
    // Integer.parseInt(params.get("page")[0]) : 1;
    // int size = params.containsKey("perpage") ?
    // Integer.parseInt(params.get("perpage")[0]) : 10;
    // String softParam = params.containsKey("sort") ? params.get("sort")[0] : null;
    // Sort sort = createSort(softParam);

    // String keyword = FilterParameter.filterKeyword(params);
    // Map<String, String> simpleFilters = FilterParameter.filterSimple(params);
    // Map<String, Map<String, String>> complexFilters =
    // FilterParameter.filterComplex(params);
    // Map<String, String> dateRangeFilters =
    // FilterParameter.filterDateRange(params);

    // logger.info("keyword: {}", keyword);
    // logger.info("simpleFilters: {}", simpleFilters);
    // logger.info("complexFilters: {}", complexFilters);
    // logger.info("dateRangeFilters: {}", dateRangeFilters);

    // Specification<UserCatalogue> specs = Specification
    // .where(BaseSpecification.<UserCatalogue>keywordSpec(keyword, "name"))
    // .and(BaseSpecification.<UserCatalogue>whereSpec(simpleFilters))
    // .and(BaseSpecification.<UserCatalogue>complexWhereSpec(complexFilters));

    // Pageable pageable = PageRequest.of(page - 1, size, sort);
    // return userCatalogueRepository.findAll(specs, pageable);
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to get all user catalogues: " +
    // e.getMessage());
    // }
    // }

    // @Override
    // public List<UserCatalogue> getAll(Map<String, String[]> params) {
    // try {
    // String sortParam = params.containsKey("sort") ? params.get("sort")[0] : null;
    // Sort sort = createSort(sortParam);

    // String keyword = FilterParameter.filterKeyword(params);
    // Map<String, String> filterSimple = FilterParameter.filterSimple(params);
    // Map<String, Map<String, String>> filterComplex =
    // FilterParameter.filterComplex(params);

    // Specification<UserCatalogue> specs = Specification
    // .where(BaseSpecification.<UserCatalogue>keywordSpec(keyword, "name"))
    // .and(BaseSpecification.<UserCatalogue>whereSpec(filterSimple))
    // .and(BaseSpecification.<UserCatalogue>complexWhereSpec(filterComplex));

    // return userCatalogueRepository.findAll(specs, sort);
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to get all user catalogues: " +
    // e.getMessage());
    // }
    // }

    // @Override
    // @Transactional
    // public Boolean delete(Long id) {
    // try {
    // userCatalogueRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));
    // userCatalogueRepository.deleteById(id);
    // return true;
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to delete user catalogue: " +
    // e.getMessage());
    // }
    // }

    // @Override
    // @Transactional
    // public Boolean deleteMutipleEntity(List<Long> ids) {
    // try {
    // List<UserCatalogue> userCatalogues =
    // userCatalogueRepository.findAllById(ids);
    // if (userCatalogues.isEmpty()) {
    // throw new EntityNotFoundException("No user catalogues found");
    // }
    // userCatalogueRepository.deleteAll(userCatalogues);
    // return true;
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to delete multiple user catalogues: " +
    // e.getMessage());
    // }
    // }
}
