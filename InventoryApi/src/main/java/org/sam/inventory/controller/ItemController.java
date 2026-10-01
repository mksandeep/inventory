package org.sam.inventory.controller;

import org.sam.inventory.dto.ItemDto;
import org.sam.inventory.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto dto) {
        return ResponseEntity.ok(service.createItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> update(@PathVariable Long id, @RequestBody ItemDto dto) {
        return ResponseEntity.ok(service.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll() {
        return ResponseEntity.ok(service.getAllItems());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam(required = false) String sku,
                                                @RequestParam(required = false) String category,
                                                @RequestParam(required = false) Integer aisle,
                                                @RequestParam(required = false) Integer bay) {
        if (sku != null) return ResponseEntity.ok(service.searchBySku(sku));
        if (category != null) return ResponseEntity.ok(service.searchByCategory(category));
        if (aisle != null && bay != null) return ResponseEntity.ok(service.searchByAisleBay(aisle, bay));
        return ResponseEntity.ok(service.getAllItems());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ItemDto>> getLowStock() {
        return ResponseEntity.ok(service.getLowStockItems());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<ItemDto>> getExpired() {
        return ResponseEntity.ok(service.getExpiredItems());
    }

    @GetMapping("/expiring/{days}")
    public ResponseEntity<List<ItemDto>> getExpiring(@PathVariable int days) {
        return ResponseEntity.ok(service.getItemsExpiringWithin(days));
    }

    @GetMapping("/total-value")
    public ResponseEntity<BigDecimal> getTotalValue() {
        return ResponseEntity.ok(service.getTotalInventoryValue());
    }
}
