package org.sam.inventory.service;

import org.sam.inventory.dto.ProductDto;
import org.sam.inventory.entity.Product;
import org.sam.inventory.mapper.ProductMapper;
import org.sam.inventory.repository.ProductRepository;
import org.sam.inventory.repository.SaleRepository;
import org.sam.inventory.util.SecurityUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final SaleRepository saleRepository;

    private ProductDto enrichWithSalesData(Product product) {
        ProductDto dto = mapper.toDto(product);
        Integer totalUnitsSold = saleRepository.findByProductId(product.getProductId()).stream()
                .mapToInt(s -> s.getUnitsSold() != null ? s.getUnitsSold() : 0)
                .sum();
        dto.setUnitsSold(totalUnitsSold);
        return dto;
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createProduct(ProductDto dto) {
        Product product = mapper.toEntity(dto);
        String currentUser = SecurityUtil.getCurrentUserEmail();
        product.setCreatedBy(currentUser);
        product.setUpdatedBy(currentUser);
        product.setCreatedTimestamp(LocalDateTime.now());
        product.setUpdatedTimestamp(LocalDateTime.now());
        return enrichWithSalesData(repository.save(product));
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setProductDescription(dto.getProductDescription());
        product.setCategory(dto.getCategory());
        product.setDimensions(dto.getDimensions());
        product.setSku(dto.getSku());
        product.setAisleNumber(dto.getAisleNumber() != null ? Integer.parseInt(dto.getAisleNumber()) : null);
        product.setBayNumber(dto.getBayNumber() != null ? Integer.parseInt(dto.getBayNumber()) : null);
        product.setStartingInventory(dto.getStartingInventory());
        product.setLowStockThreshold(dto.getLowStockThreshold());
        product.setUnitCost(dto.getUnitCost());
        product.setMarkup(dto.getMarkup());
        product.setExpiryDate(dto.getExpiryDate());
        product.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
        product.setUpdatedTimestamp(LocalDateTime.now());
        return enrichWithSalesData(repository.save(product));
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    // @Cacheable(value = "productById", key = "#id") - Disabled: using NoOpCacheManager
    public ProductDto getProductById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return enrichWithSalesData(product);
    }

    // @Cacheable(value = "products") - Disabled: using NoOpCacheManager
    public List<ProductDto> getAllProducts() {
        return repository.findAll().stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "productsBySku", key = "#sku") - Disabled: using NoOpCacheManager
    public List<ProductDto> searchBySku(String sku) {
        return repository.findBySku(sku).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "productsByCategory", key = "#category") - Disabled: using NoOpCacheManager
    public List<ProductDto> searchByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "productsByAisleBay", key = "#aisle + '-' + #bay") - Disabled: using NoOpCacheManager
    public List<ProductDto> searchByAisleBay(String aisle, String bay) {
        Integer aisleNum = aisle != null ? Integer.parseInt(aisle) : null;
        Integer bayNum = bay != null ? Integer.parseInt(bay) : null;
        return repository.findByAisleNumberAndBayNumber(aisleNum, bayNum).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "lowStockProducts") - Disabled: using NoOpCacheManager
    public List<ProductDto> getLowStockProducts() {
        return repository.findLowStockProducts().stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "expiredProducts") - Disabled: using NoOpCacheManager
    public List<ProductDto> getExpiredProducts() {
        return repository.findExpiredProducts(LocalDate.now()).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "expiringProducts", key = "#days") - Disabled: using NoOpCacheManager
    public List<ProductDto> getProductsExpiringWithin(int days) {
        LocalDate now = LocalDate.now();
        return repository.findProductsExpiringWithin(now, now.plusDays(days)).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "totalInventoryValue") - Disabled: using NoOpCacheManager
    public BigDecimal getTotalInventoryValue() {
        return repository.findAll().stream()
                .map(Product::getInventoryValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // @Cacheable(value = "productsByDateRange", key = "#startDate + '-' + #endDate") - Disabled: using NoOpCacheManager
    public List<ProductDto> getProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findProductsByDateRange(startDate, endDate).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }

    // @Cacheable(value = "productsByUpdateDateRange", key = "#startDate + '-' + #endDate") - Disabled: using NoOpCacheManager
    public List<ProductDto> getProductsByUpdateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findProductsByUpdateDateRange(startDate, endDate).stream()
                .map(this::enrichWithSalesData)
                .collect(Collectors.toList());
    }
}
