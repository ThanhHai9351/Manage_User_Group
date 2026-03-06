package com.example.springboot_demo.modules.users.reponsitories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springboot_demo.modules.users.entities.UserCatalogue;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface UserCatalogueRepository
        extends JpaRepository<UserCatalogue, Long>, JpaSpecificationExecutor<UserCatalogue> {

}
