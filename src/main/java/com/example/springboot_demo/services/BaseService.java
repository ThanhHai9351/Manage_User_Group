package com.example.springboot_demo.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

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

}
