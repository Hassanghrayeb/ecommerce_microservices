package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.UserPreference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends CrudRepository<UserPreference, Long>
{
    Optional<UserPreference> findByLoginId(Long loginId);
}
