package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.UserPreferenceDTO;
import com.hamster.ecommerce.model.entity.UserPreference;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserPreferenceMapper
{
    public abstract UserPreference dtoToEntity(UserPreferenceDTO dto);

    public abstract UserPreferenceDTO entityToDTO(UserPreference entity);
}
