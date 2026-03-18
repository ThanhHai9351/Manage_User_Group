package com.example.springboot_demo.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot_demo.helpers.FilterParameter;
import com.example.springboot_demo.mappers.BaseMapper;
import com.example.springboot_demo.specifications.BaseSpecification;

/**
 * Generic base service with repository/mapping/CRUD logic.
 * R = Repository (must extend JpaRepository + JpaSpecificationExecutor)
 * M = Mapper (with toEntity(C) method)
 * T = Entity type
 * C = Create DTO type
 * U = Update DTO type
 */
@Service
public abstract class BaseService<T, // Entity
        M extends BaseMapper<T, ?, C, U>, // Mapper type (should implement toEntity(C))
        C, // CreateDTO
        U, // UpdateDTO
        R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T> // Repository Type
> {

    protected abstract String[] getSearchFields();

    protected abstract R getRepository();

    protected abstract M getMapper();

    protected Sort createSort(String sortParam) {
        if (sortParam == null || sortParam.isEmpty()) {
            return Sort.by(Sort.Order.desc("id"));
        }

        String[] parts = sortParam.split(",");
        String field = parts[0];
        String sortDirection = parts.length > 1 ? parts[1] : "asc";

        if ("desc".equalsIgnoreCase(sortDirection)) {
            return Sort.by(Sort.Order.desc(field));
        }
        return Sort.by(Sort.Order.asc(field));
    }

    public List<T> getAll(Map<String, String[]> parameters) {
        Sort sort = parseSort(parameters);
        Specification<T> specification = buildSpecification(parameters, getSearchFields());
        return getRepository().findAll(specification, sort);
    }

    public Page<T> pagination(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1;
        int perpage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 20;
        Sort sort = parseSort(parameters);
        Specification<T> specs = buildSpecification(parameters, getSearchFields());

        Pageable pageable = PageRequest.of(page - 1, perpage, sort);
        return getRepository().findAll(specs, pageable);
    }

    @Transactional
    public T create(C createRequest) {
        // Requires M to have a method toEntity(C), usually via interface/abstract class
        try {
            @SuppressWarnings("unchecked")
            T entity = (T) getMapper().getClass().getMethod("toEntity", createRequest.getClass()).invoke(getMapper(),
                    createRequest);
            return getRepository().save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert and save entity: " + e.getMessage(), e);
        }
    }

    @Transactional
    public T update(Long id, U updateRequest) {
        T entity = ((JpaRepository<T, Long>) getRepository()).findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found"));
        getMapper().updateEntityFromRequest(updateRequest, entity);
        return getRepository().save(entity);
    }

    @Transactional
    public Boolean delete(Long id) {
        T entity = ((JpaRepository<T, Long>) getRepository()).findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found"));
        getRepository().delete(entity);
        return true;
    }

    @Transactional
    public Boolean deleteMutipleEntity(List<Long> ids) {
        List<T> entities = ((JpaRepository<T, Long>) getRepository()).findAllById(ids);
        if (entities.isEmpty()) {
            throw new RuntimeException("Entities not found");
        }
        getRepository().deleteAll(entities);
        return true;
    }

    protected Sort parseSort(Map<String, String[]> parameters) {
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null;
        return createSort(sortParam);
    }

    protected Specification<T> buildSpecification(Map<String, String[]> parameters, String[] searchFields) {
        String keyword = FilterParameter.filterKeyword(parameters);
        Map<String, String> filterSimple = FilterParameter.filterSimple(parameters);
        Map<String, Map<String, String>> filterComplex = FilterParameter.filterComplex(parameters);

        return Specification.where(BaseSpecification.<T>keywordSpec(keyword, searchFields))
                .and(BaseSpecification.<T>whereSpec(filterSimple))
                .and(BaseSpecification.<T>complexWhereSpec(filterComplex));
    }

}
