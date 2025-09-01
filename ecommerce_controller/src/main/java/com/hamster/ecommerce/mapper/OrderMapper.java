package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.OrderDTO;
import com.hamster.ecommerce.model.entity.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper
{
    public abstract Order dtoToEntity(OrderDTO dto);

    public abstract OrderDTO entityToDTO(Order entity);

    public abstract List<OrderDTO> entityToDTOList(List<OrderDTO> entityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Order dtoToEntity(OrderDTO dto, @MappingTarget Order target);
}
