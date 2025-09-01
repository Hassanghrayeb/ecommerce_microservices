package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.entity.UserPreference;

public interface UserPreferenceService
{
    UserPreference getUserPreferenceByUser();

    UserPreference saveUserPreference(UserPreference userPreference);
}
