package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.PersonDTO;
import com.hamster.ecommerce.model.entity.Person;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PersonMapper
{
    @Mapping(target = "loginId", source = "userId")
    public abstract Person dtoToEntity(PersonDTO dto);

    public abstract PersonDTO entityToDTO(Person entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Person dtoToEntity(PersonDTO dto, @MappingTarget Person target);
}
