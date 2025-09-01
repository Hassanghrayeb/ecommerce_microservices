package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.Person;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long>
{
    Optional<Person> findByLoginId(Long loginId);
}
