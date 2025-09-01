package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.model.entity.Role;
import com.hamster.ecommerce.repository.PermissionRepository;
import com.hamster.ecommerce.repository.RoleRepository;
import com.hamster.ecommerce.service.RoleService;
import com.hamster.ecommerce.util.ContextUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService
{

    private final RoleRepository roleRepository;
    private final ContextUtil contextUtil;
    private final PermissionRepository permissionRepository;


    public RoleServiceImpl(RoleRepository roleRepository,
            ContextUtil contextUtil, PermissionRepository permissionRepository)
    {
        this.roleRepository = roleRepository;
        this.contextUtil = contextUtil;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<Role> find(Pageable pageable)
    {
        Page<Role> roles = roleRepository.findAll(pageable);
        roles.iterator().forEachRemaining(role ->
                role.setPermissions(permissionRepository.findByRoleId(role.getId())));
        return roles;
    }

    @Override
    public Optional<Role> findById(Long id)
    {
        return roleRepository.findById(id).map(role ->
        {
            role.setPermissions(permissionRepository.findByRoleId(role.getId()));
            return role;
        });
    }

    @Override
    public List<Role> findByUserId(Long userId)
    {
        return roleRepository.findByLoginId(userId).stream().map(role ->
        {
            role.setPermissions(permissionRepository.findByRoleId(role.getId()));
            return role;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean doRolesExist(Long[] roleIdList)
    {
        for (Long roleId : roleIdList)
        {
            if (roleRepository.findById(roleId).isEmpty())
                return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Role save(Role role)
    {
        if (role.isSystemRole() == null)
            role.setSystemRole(Boolean.FALSE);

        role.setUpdateLoginId(contextUtil.getCurrentUserId());
        role.setUpdateTimestamp(LocalDateTime.now());

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void setRolePermissions(Long roleId, List<Long> permissionIdList)
    {
        roleRepository.deleteRolePermissionsByRoleId(roleId);
        for (Long permissionId : permissionIdList)
            roleRepository.insertRolePermissionRecord(roleId, permissionId);
    }

    @Override
    public void deleteById(Long id)
    {
        roleRepository.deleteById(id);
    }

    @Override
    public Optional<Role> findByName(String freeUser)
    {
        return roleRepository.findByName(freeUser);
    }

}
