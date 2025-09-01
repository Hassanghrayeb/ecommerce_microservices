package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.RoleMapper;
import com.hamster.ecommerce.model.dto.RoleDTO;
import com.hamster.ecommerce.model.entity.Role;
import com.hamster.ecommerce.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "Role Controller", description = "Operations pertaining to roles")
@RestController
@RequestMapping("/admin/role")
public class RoleController
{
    private final RoleMapper roleMapper;
    private final RoleService roleService;

    public RoleController(RoleMapper roleMapper, RoleService roleService)
    {
        this.roleMapper = roleMapper;
        this.roleService = roleService;
    }

    @Operation(summary = "Find all roles, in paged format")
    @GetMapping()
    ResponseEntity<Page<RoleDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Role> rolePage = roleService.find(pageable);
        return new ResponseEntity<>(rolePage.map(roleMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Find one role by its id")
    @GetMapping("/{id}")
    ResponseEntity<RoleDTO> findOne(@PathVariable Long id)
    {
        Role role = roleService.findById(id).orElseThrow(() -> new NotFoundException(id));
        return new ResponseEntity<>(roleMapper.entityToDTO(role), HttpStatus.OK);
    }

    @Operation(summary = "Find all roles for a given user's id")
    @GetMapping("/user/{userId}")
    ResponseEntity<List<RoleDTO>> findByUserId(@PathVariable Long userId)
    {
        List<Role> roleList = roleService.findByUserId(userId);
        return new ResponseEntity<>(roleMapper.entityToDTOList(roleList), HttpStatus.OK);
    }

    @Operation(summary = "Create a new role")
    @PostMapping("")
    ResponseEntity<RoleDTO> saveRole(@Valid @RequestBody RoleDTO dto)
    {
        dto.setId(null);
        Role role = roleMapper.dtoToEntity(dto);
        role = roleService.save(role);
        return new ResponseEntity<>(roleMapper.entityToDTO(role), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing role")
    @PutMapping("/{id}")
    ResponseEntity<Object> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO dto)
    {
        Optional<Role> optionalRole = roleService.findById(id);
        if (optionalRole.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Role role = optionalRole.get();

        if (role.isSystemRole())
            return new ResponseEntity<>("You may not update a system role.", HttpStatus.FORBIDDEN);

        dto.setId(id);
        roleMapper.dtoToEntity(dto, role);
        role = roleService.save(role);
        return new ResponseEntity<>(roleMapper.entityToDTO(role), HttpStatus.OK);
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteItem(@PathVariable Long id)
    {
        Optional<Role> optionalRole = roleService.findById(id);
        if (optionalRole.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Role role = optionalRole.get();

        if (Objects.nonNull(role.isSystemRole()) && role.isSystemRole())
            return new ResponseEntity<>("You may not delete a system role.", HttpStatus.FORBIDDEN);

        roleService.deleteById(id);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @Operation(summary = "Set the permissions for a given role, by an array of permission ids")
    @PutMapping("/{roleId}/permissions/{permissionIdList}")
    ResponseEntity<Object> setRolePermissions(@PathVariable Long roleId, @PathVariable Long[] permissionIdList)
    {
        Optional<Role> optionalRole = roleService.findById(roleId);
        if (optionalRole.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Role role = optionalRole.get();

        if (Objects.nonNull(role.isSystemRole()) && role.isSystemRole())
            return new ResponseEntity<>("You may not delete a system role.", HttpStatus.FORBIDDEN);

        roleService.setRolePermissions(roleId, Arrays.asList(permissionIdList));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
