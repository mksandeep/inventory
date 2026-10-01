package org.sam.inventory.mapper;

import org.sam.inventory.dto.PurchaseDto;
import org.sam.inventory.entity.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    PurchaseDto toDto(Purchase purchase);
    Purchase toEntity(PurchaseDto dto);
}

