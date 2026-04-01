package com.example.springboot_demo.modules.users.resources;

import lombok.Data;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@RequiredArgsConstructor
public class PermissionResource {
    private final Long id;
    private final String name;
    private final Integer publish;
}
