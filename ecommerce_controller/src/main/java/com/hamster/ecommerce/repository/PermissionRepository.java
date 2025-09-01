package com.hamster.ecommerce.repository;


import com.hamster.ecommerce.model.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long>
{
    @Query(value = "select p.* from permission p inner join role_permission rp on p.id = " +
            "rp.permission_id where rp.role_id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    Page<Permission> findAll(Pageable pageable);
}
