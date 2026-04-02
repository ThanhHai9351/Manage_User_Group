package com.example.springboot_demo.services;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    protected abstract String[] getSearchFields();

    protected String[] getRelations() {
        return new String[0];
    };

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
        logger.info("Creating...");
        T payload = getMapper().toEntity(createRequest);
        T entity = getRepository().save(payload); // thêm mới vào trong bảng gốc.
        handleManyToManyRelations(entity, createRequest);
        return entity;
    }

    private void handleManyToManyRelations(T entity, Object request) {
        String[] relations = getRelations();
        if (relations != null && relations.length > 0) {
            for (String relation : relations) {
                try {
                    Field requestField = request.getClass().getDeclaredField(relation);
                    requestField.setAccessible(true);

                    @SuppressWarnings("unchecked")
                    List<Long> ids = (List<Long>) requestField.get(request);
                    if (ids != null && !ids.isEmpty()) {
                        Field entityField = entity.getClass().getDeclaredField(relation);
                        entityField.setAccessible(true);

                        // Get the generic type of the relation (e.g., Permission)
                        ParameterizedType setType = (ParameterizedType) entityField.getGenericType();
                        Class<?> entityClass = (Class<?>) setType.getActualTypeArguments()[0];

                        // Build repository bean name (e.g., permissionRepository)
                        String repositoryName = entityClass.getSimpleName() + "Repository";
                        repositoryName = Character.toLowerCase(repositoryName.charAt(0)) + repositoryName.substring(1);

                        // Get repository bean from Spring context
                        @SuppressWarnings("unchecked")
                        JpaRepository<Object, Long> repository = (JpaRepository<Object, Long>) applicationContext
                                .getBean(repositoryName);

                        // Retrieve related entities by IDs
                        List<Object> entites = repository.findAllById(ids);

                        // Set the relation in the entity (assuming Set/Collection)
                        // Use a set to avoid duplicates if the relation is a Set
                        if (entityField.getType().isAssignableFrom(List.class)) {
                            entityField.set(entity, entites);
                        } else if (entityField.getType().isAssignableFrom(Set.class)) {
                            entityField.set(entity, new HashSet<>(entites));
                        } else {
                            entityField.set(entity, entites);
                        }
                    }
                } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to handle many-to-many relations: " + e.getMessage());
                }
            }
        }

    }

    @Transactional
    public T update(Long id, U request) {
        T entity = ((JpaRepository<T, Long>) getRepository()).findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found"));
        getMapper().updateEntityFromRequest(request, entity);

        T entityUpdate = getRepository().save(entity);
        handleManyToManyRelations(entityUpdate, request);
        return entityUpdate;
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
