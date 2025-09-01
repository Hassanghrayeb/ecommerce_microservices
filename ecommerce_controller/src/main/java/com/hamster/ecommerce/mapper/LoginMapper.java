package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.LoginCustomDTO;
import com.hamster.ecommerce.model.dto.LoginDTO;
import com.hamster.ecommerce.model.entity.Login;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LoginMapper
{
    public abstract List<Login> dtoToEntityList(List<LoginDTO> dtoList);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "salt", ignore = true)
    public abstract List<LoginDTO> entityToDTOList(List<Login> entityList);

    @Mapping(target = "authorities", ignore = true)
    public abstract Login dtoToEntity(LoginDTO dto);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract Login customDtoToEntity(LoginCustomDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "authorities", ignore = true)
    public abstract void dtoToEntity(LoginDTO dto, @MappingTarget Login login);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract void customDtoToEntity(LoginCustomDTO dto, @MappingTarget Login login);

    @Mapping(target = "password", ignore = true)
    public abstract LoginDTO entityToDTO(Login entity);

    @AfterMapping
    protected void removePasswordFromDTO(@MappingTarget LoginDTO loginDTO)
    {
        loginDTO.setPassword("<omitted>");
    }
}
