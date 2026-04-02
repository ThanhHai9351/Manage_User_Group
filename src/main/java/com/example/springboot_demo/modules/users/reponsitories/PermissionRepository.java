package com.example.springboot_demo.modules.users.reponsitories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springboot_demo.modules.users.entities.Permission;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository(value = "permissionRepository")
public interface PermissionRepository
        extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

}
