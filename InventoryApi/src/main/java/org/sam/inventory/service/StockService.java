package org.sam.inventory.service;

import org.sam.inventory.dto.StockDto;
import org.sam.inventory.entity.Stock;
import org.sam.inventory.mapper.StockMapper;
import org.sam.inventory.repository.StockRepository;
import org.sam.inventory.util.SecurityUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository repository;
    private final StockMapper mapper;

    @CacheEvict(value = "stocks", allEntries = true)
    public StockDto createStock(StockDto dto) {
        Stock stock = mapper.toEntity(dto);
        String currentUser = SecurityUtil.getCurrentUserEmail();
        stock.setCreatedBy(currentUser);
        stock.setUpdatedBy(currentUser);
        stock.setCreatedTimestamp(LocalDateTime.now());
        stock.setUpdatedTimestamp(LocalDateTime.now());
        return mapper.toDto(repository.save(stock));
    }

    @CacheEvict(value = "stocks", allEntries = true)
    public StockDto updateStock(Long id, StockDto dto) {
        Stock stock = repository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found"));
        stock.setProductDescription(dto.getProductDescription());
        stock.setCategory(dto.getCategory());
        stock.setDimensions(dto.getDimensions());
        stock.setUpc(dto.getUpc());
        stock.setSku(dto.getSku());
        stock.setAisleNumber(dto.getAisleNumber() != null ? Integer.parseInt(dto.getAisleNumber()) : null);
        stock.setBayNumber(dto.getBayNumber() != null ? Integer.parseInt(dto.getBayNumber()) : null);
        stock.setStartingInventory(dto.getStartingInventory());
        stock.setLowStockThreshold(dto.getLowStockThreshold());
        stock.setUnitCost(dto.getUnitCost());
        stock.setMarkup(dto.getMarkup());
        stock.setExpiryDate(dto.getExpiryDate());
        stock.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
        stock.setUpdatedTimestamp(LocalDateTime.now());
        return mapper.toDto(repository.save(stock));
    }

    @CacheEvict(value = "stocks", allEntries = true)
    public void deleteStock(Long id) {
        repository.deleteById(id);
    }

    public StockDto getStockById(Long id) {
        Stock stock = repository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found"));
        return mapper.toDto(stock);
    }

    public StockDto getStockByUpc(String upc) {
        Stock stock = repository.findByUpc(upc).orElseThrow(() -> new RuntimeException("Stock with UPC " + upc + " not found"));
        return mapper.toDto(stock);
    }

    public List<StockDto> getAllStocks() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> searchBySku(String sku) {
        return repository.findBySku(sku).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> searchByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> searchByAisleBay(String aisle, String bay) {
        try {
            Integer aisleNum = Integer.parseInt(aisle);
            Integer bayNum = Integer.parseInt(bay);
            return repository.findByAisleNumberAndBayNumber(aisleNum, bayNum).stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid aisle or bay number");
        }
    }

    public List<StockDto> getLowStockProducts() {
        return repository.findLowStockProducts().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> getExpiredProducts() {
        return repository.findExpiredProducts(LocalDate.now()).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> getProductsExpiringWithin(int days) {
        LocalDate now = LocalDate.now();
        LocalDate future = now.plusDays(days);
        return repository.findProductsExpiringWithin(now, future).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalInventoryValue() {
        return repository.findAll().stream()
                .map(s -> s.getInventoryValue() != null ? s.getInventoryValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<StockDto> getStocksByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findStockByDateRange(startDate, endDate).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> getStocksByUpdateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findStockByUpdateDateRange(startDate, endDate).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "stocks", allEntries = true)
    public void updateStockLevelByUpc(String upc, Integer quantityChange) {
        Stock stock = repository.findByUpc(upc)
                .orElseThrow(() -> new RuntimeException("Stock with UPC " + upc + " not found"));
        Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
        stock.setCurrentStockLevel(currentLevel + quantityChange);
        stock.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
        repository.save(stock);
    }
}

