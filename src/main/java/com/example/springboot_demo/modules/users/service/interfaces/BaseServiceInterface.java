package com.example.springboot_demo.modules.users.service.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface BaseServiceInterface<E, C, U> {
    E create(C request);

    E update(Long id, U request);

    Boolean delete(Long id);

    Boolean deleteMutipleEntity(List<Long> ids);

    Page<E> pagination(Map<String, String[]> parameters);

    List<E> getAll(Map<String, String[]> parameters);
}
