package com.hamster.ecommerce.repository;


import com.hamster.ecommerce.model.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>
{

    @Query(value = "select r.* from role r inner join login_role lr on r.id = lr.role_id where lr.login_id = :loginId")
    List<Role> findByLoginId(@Param("loginId") Long loginId);

    Page<Role> findAll(Pageable pageable);

    @Modifying
    @Query(value = "delete from role_permission where role_id = :roleId")
    void deleteRolePermissionsByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query(value = "insert into role_permission (role_id, permission_id) values(:roleId, :permissionId)")
    void insertRolePermissionRecord(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    Optional<Role> findByName(String name);
}
