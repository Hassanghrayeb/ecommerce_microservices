package com.hamster.ecommerce.repository;


import com.hamster.ecommerce.model.entity.FailedLogin;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FailedLoginRepository extends CrudRepository<FailedLogin, Long>
{
    Optional<FailedLogin> findByLoginId(Long loginId);

    @Modifying
    @Query(value = "delete from failed_login where login_id = :loginId")
    void deleteExistingByUserId(@Param("loginId") Long loginId);

    @Modifying
    @Query(value = "insert into failed_login values (:loginId, :failCount, :timestamp)")
    void saveNewItem(@Param("loginId") Long loginId, @Param("failCount") Integer failCount, @Param("timestamp") LocalDateTime timestamp);
}
