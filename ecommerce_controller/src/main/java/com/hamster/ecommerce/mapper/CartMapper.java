package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.CartDTO;
import com.hamster.ecommerce.model.entity.Cart;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CartMapper
{
    public abstract Cart dtoToEntity(CartDTO dto);

    public abstract CartDTO entityToDTO(Cart entity);

    public abstract List<CartDTO> entityToDTOList(List<Cart> entityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Cart dtoToEntity(CartDTO dto, @MappingTarget Cart target);
}
