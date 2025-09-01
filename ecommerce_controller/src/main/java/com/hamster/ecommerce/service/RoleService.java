package com.hamster.ecommerce.service;


import com.hamster.ecommerce.model.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleService
{
    Page<Role> find(Pageable pageable);

    Optional<Role> findById(Long id);

    List<Role> findByUserId(Long userId);

    Boolean doRolesExist(Long[] roleIdList);

    Role save(Role role);

    void setRolePermissions(Long roleIdId, List<Long> permissionIdList);

    void deleteById(Long id);

    Optional<Role> findByName(String freeUser);
}
