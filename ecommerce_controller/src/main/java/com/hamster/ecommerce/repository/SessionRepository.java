package com.hamster.ecommerce.repository;


import com.hamster.ecommerce.model.entity.Session;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, Long>
{
    Optional<Session> findByAccessToken(String accessToken);

    Optional<Session> findByRefreshToken(String refreshToken);

    @Modifying
    @Query(value = "insert into session values(:username, :consecutiveRefreshes, :accessToken, :refreshToken, :createDateTime) on conflict (username) do update set consecutive_refreshes = :consecutiveRefreshes, access_token = :accessToken, refresh_token = :refreshToken, create_datetime = :createDateTime")
    void save(@Param("username") String username, @Param("consecutiveRefreshes") int consecutiveRefreshes, @Param("accessToken") String accessToken, @Param("refreshToken") String refreshToken, @Param("createDateTime") LocalDateTime createDateTime);

    @Modifying
    @Query(value = "delete from session where access_token = :accessToken")
    void deleteByAccessToken(@Param("accessToken") String accessToken);

    @Modifying
    @Query(value = "delete from session where refresh_token = :refreshToken")
    void deleteByRefreshToken(@Param("refreshToken") String refreshToken);

    @Modifying
    @Query(value = "delete from session where username = :username")
    void deleteExistingSessionsForUser(@Param("username") String username);

    /*------------------------------------------------------+
    |   Delete all sessions created more than 2 hours ago   |
    +------------------------------------------------------*/
    @Modifying
    @Query(value = "delete from session where create_datetime > now() - interval '2 hours'")
    void deleteStaleSessions();
}
