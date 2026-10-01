package org.sam.inventory.service;

import org.sam.inventory.dto.ItemDto;
import org.sam.inventory.entity.Item;
import org.sam.inventory.mapper.ItemMapper;
import org.sam.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;
    private final ItemMapper mapper;

    public ItemDto createItem(ItemDto dto) {

        Item item = mapper.toEntity(dto);
        return mapper.toDto(repository.save(item));
    }

    public ItemDto updateItem(Long id, ItemDto dto) {
        Item item = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        // Update fields, but recalculate in @PreUpdate
        item.setItemDescription(dto.getItemDescription());
        item.setCategory(dto.getCategory());
        item.setDimensions(dto.getDimensions());
        item.setSku(dto.getSku());
        item.setAisleNumber(dto.getAisleNumber());
        item.setBayNumber(dto.getBayNumber());
        item.setStartingInventory(dto.getStartingInventory());
        item.setLowStockThreshold(dto.getLowStockThreshold());
        item.setUnitCost(dto.getUnitCost());
        item.setMarkup(dto.getMarkup());
        item.setExpiryDate(dto.getExpiryDate());
        return mapper.toDto(repository.save(item));
    }

    public ItemDto getItemById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow());
    }

    public List<ItemDto> getAllItems() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public void deleteItem(Long id) {
        repository.deleteById(id);
    }

    public List<ItemDto> searchBySku(String sku) {
        return repository.findBySku(sku).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchByCategory(String category) {
        return repository.findByCategory(category).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchByAisleBay(Integer aisle, Integer bay) {
        return repository.findByAisleNumberAndBayNumber(aisle, bay).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ItemDto> getLowStockItems() {
        return repository.findLowStockItems().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ItemDto> getExpiredItems() {
        return repository.findExpiredItems(LocalDate.now()).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ItemDto> getItemsExpiringWithin(int days) {
        LocalDate now = LocalDate.now();
        return repository.findItemsExpiringWithin(now, now.plusDays(days)).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public BigDecimal getTotalInventoryValue() {
        return repository.findAll().stream()
                .map(Item::getInventoryValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
