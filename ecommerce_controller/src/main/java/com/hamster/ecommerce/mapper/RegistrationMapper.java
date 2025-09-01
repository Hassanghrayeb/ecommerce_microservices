package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.UserRegistrationDTO;
import com.hamster.ecommerce.model.entity.UserRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RegistrationMapper
{

    public abstract UserRegistration dtoToEntity(UserRegistrationDTO dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    @Mapping(target = "id", source= "id")
    public abstract UserRegistrationDTO entityToDTO(UserRegistration entity);
}
