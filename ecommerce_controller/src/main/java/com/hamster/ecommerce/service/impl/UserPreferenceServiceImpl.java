package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.AccessDeniedException;
import com.hamster.ecommerce.model.entity.UserPreference;
import com.hamster.ecommerce.repository.UserPreferenceRepository;
import com.hamster.ecommerce.service.UserPreferenceService;
import com.hamster.ecommerce.util.ContextUtil;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService
{

    private final ContextUtil contextUtil;
    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceServiceImpl(ContextUtil contextUtil, UserPreferenceRepository userPreferenceRepository)
    {
        this.contextUtil = contextUtil;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Override
    public UserPreference getUserPreferenceByUser()
    {
        Long userId = this.contextUtil.getCurrentUserId();

        if (userId < 0)
        {
            throw new AccessDeniedException();
        }

        return userPreferenceRepository.findByLoginId(userId).orElse(UserPreference.getDefaultUserPreference());

    }

    @Override
    public UserPreference saveUserPreference(UserPreference userPreference)
    {
        Long userId = this.contextUtil.getCurrentUserId();

        if (userId < 0)
        {
            throw new AccessDeniedException();
        }

        UserPreference userPreferenceToSave = userPreferenceRepository.findByLoginId(userId).orElse(new UserPreference());
        userPreferenceToSave.setLoginId(userId);
        userPreferenceToSave.setTheme(userPreference.getTheme());
        userPreferenceToSave.setLanguage(userPreference.getLanguage());

        return userPreferenceRepository.save(userPreferenceToSave);
    }
}
