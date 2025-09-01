package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.model.entity.Permission;
import com.hamster.ecommerce.repository.PermissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Permission Controller", description = "Operations pertaining to permissions")
@RestController
@RequestMapping("/admin/permission")
public class PermissionController
{
    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository)
    {
        this.permissionRepository = permissionRepository;
    }

    @Operation(summary = "Find all permissions, in paged format")
    @GetMapping()
    ResponseEntity<Iterable<Permission>> findAllPaged()
    {
        return new ResponseEntity<>(permissionRepository.findAll(), HttpStatus.OK);
    }

    @Operation(summary = "Find one permission by its id")
    @GetMapping("/{id}")
    ResponseEntity<Permission> findOne(@PathVariable Long id)
    {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return new ResponseEntity<>(permission, HttpStatus.OK);
    }
}
