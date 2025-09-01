package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.UserRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends
        PagingAndSortingRepository<UserRegistration, Long>, CrudRepository<UserRegistration, Long>
{
    @Query("select * FROM user_registration WHERE lower(email_address) = lower(:emailAddress)")
    Optional<UserRegistration> findByEmail(@Param("emailAddress") String emailAddress);
    Page<UserRegistration> findByStatus(String status,Pageable pageable);
}
