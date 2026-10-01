package org.sam.inventory.mapper;

import org.sam.inventory.dto.SaleDto;
import org.sam.inventory.entity.Sale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    SaleDto toDto(Sale sale);
    Sale toEntity(SaleDto dto);
}

