package com.hamster.ecommerce.mapper;

import com.hamster.ecommerce.model.dto.ProductDTO;
import com.hamster.ecommerce.model.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProductMapper
{
    public abstract Product dtoToEntity(ProductDTO dto);

    public abstract ProductDTO entityToDTO(Product entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Product dtoToEntity(ProductDTO dto, @MappingTarget Product target);
}
