package com.hamster.ecommerce.repository;


import com.hamster.ecommerce.model.entity.Login;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginRepository extends PagingAndSortingRepository<Login, Long>, CrudRepository<Login, Long>
{
    String usersWithRoleQuery = "select l.* from login l inner join login_role lr on l.id = lr.login_id " +
            "inner join role r on lr.role_id = r.id where lower(r.name) = lower(:roleName) limit :limit offset :offset";

    @Query(value = "select * from login where lower(username) = lower(:username)")
    Optional<Login> findByUsername(String username);

    @Query(value = usersWithRoleQuery)
    List<Login> getUsersWithRole(@Param("roleName") String roleName,
            @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "select exists(select 1 from login where lower(username) = lower(:username))")
    Boolean doesUsernameExist(@Param("username") String username);

    @Modifying
    @Query(value = "delete from login_role where login_id = :loginId")
    void deleteUserRolesByLoginId(@Param("loginId") Long loginId);

    @Modifying
    @Query(value = "insert into login_role (login_id, role_id, update_login_id, update_timestamp) " +
            "values(:loginId, :roleId, :createLoginId, :updateTimestamp)")
    void insertUserRoleRecord(@Param("loginId") Long loginId, @Param("roleId") Long roleId,
            @Param("createLoginId") Long createLoginId, @Param("updateTimestamp") LocalDateTime updateTimestamp);

    @Query(value = "select exists(select 1 from login_role where login_id = :loginId and role_id = :roleId)")
    Boolean doesLoginRoleExist(@Param("loginId") Long loginId, @Param("roleId") Long roleId);
}
