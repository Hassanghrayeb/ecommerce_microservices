package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.dto.SimpleIdStatusDTO;
import com.hamster.ecommerce.model.entity.Login;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoginService
{
    Page<Login> find(Pageable pageable);

    Optional<Login> findByUsername(String username);

    Optional<Login> findById(Long id);

    Page<Login> getUsersWithRole(Pageable pageable, String roleName);

    Login save(Login login, boolean passwordIsPlainText);

    Boolean doesUsernameExist(String username);

    void setLoginRoles(Long userId, List<Long> roleIdList);

    void updateStatus(SimpleIdStatusDTO dto);
}
