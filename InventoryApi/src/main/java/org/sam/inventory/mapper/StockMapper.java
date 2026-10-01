package org.sam.inventory.mapper;

import org.sam.inventory.dto.StockDto;
import org.sam.inventory.entity.Stock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMapper {
    StockDto toDto(Stock stock);
    Stock toEntity(StockDto dto);
}

