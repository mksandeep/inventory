package org.sam.inventory.mapper;

import org.sam.inventory.dto.ItemDto;
import org.sam.inventory.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);
    Item toEntity(ItemDto dto);
}
