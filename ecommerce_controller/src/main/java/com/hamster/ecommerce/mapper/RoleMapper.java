package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.RoleDTO;
import com.hamster.ecommerce.model.entity.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RoleMapper
{
    public abstract List<Role> dtoToEntityList(List<RoleDTO> dtoList);

    public abstract List<RoleDTO> entityToDTOList(List<Role> entityList);

    public abstract Role dtoToEntity(RoleDTO dto);

    //This essentially lets us do a patch of new values into a persistent object
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void dtoToEntity(RoleDTO dto, @MappingTarget Role entity);

    public abstract RoleDTO entityToDTO(Role entity);
}
