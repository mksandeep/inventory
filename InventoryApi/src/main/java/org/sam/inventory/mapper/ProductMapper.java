package org.sam.inventory.mapper;

import org.sam.inventory.dto.ProductDto;
import org.sam.inventory.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toEntity(ProductDto dto);
}
