package org.sam.inventory.controller;

import org.sam.inventory.dto.StockDto;
import org.sam.inventory.dto.BulkUploadResponse;
import org.sam.inventory.service.StockService;
import org.sam.inventory.service.ExcelUploadService;
import org.sam.inventory.service.ExcelTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService service;

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<StockDto> create(@RequestBody StockDto dto) {
        return ResponseEntity.ok(service.createStock(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<StockDto> update(@PathVariable Long id, @RequestBody StockDto dto) {
        return ResponseEntity.ok(service.updateStock(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<StockDto>> getStocksByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate + "T00:00:00");
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate + "T23:59:59");
            return ResponseEntity.ok(service.getStocksByDateRange(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStockById(id));
    }

    @GetMapping("/by-upc/{upc}")
    public ResponseEntity<StockDto> getByUpc(@PathVariable String upc) {
        return ResponseEntity.ok(service.getStockByUpc(upc));
    }

    @GetMapping
    public ResponseEntity<List<StockDto>> getAll() {
        return ResponseEntity.ok(service.getAllStocks());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StockDto>> search(@RequestParam(required = false) String sku,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String aisle,
                                                  @RequestParam(required = false) String bay) {
        if (sku != null) return ResponseEntity.ok(service.searchBySku(sku));
        if (category != null) return ResponseEntity.ok(service.searchByCategory(category));
        if (aisle != null && bay != null) return ResponseEntity.ok(service.searchByAisleBay(aisle, bay));
        return ResponseEntity.ok(service.getAllStocks());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<StockDto>> getLowStock() {
        return ResponseEntity.ok(service.getLowStockProducts());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<StockDto>> getExpired() {
        return ResponseEntity.ok(service.getExpiredProducts());
    }

    @GetMapping("/expiring/{days}")
    public ResponseEntity<List<StockDto>> getExpiring(@PathVariable int days) {
        return ResponseEntity.ok(service.getProductsExpiringWithin(days));
    }

    @GetMapping("/total-value")
    public ResponseEntity<BigDecimal> getTotalValue() {
        return ResponseEntity.ok(service.getTotalInventoryValue());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BulkUploadResponse> uploadStocks(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(excelUploadService.uploadStocks(file));
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] template = excelTemplateService.generateStockTemplate();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stocks_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{upc}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> adjustStock(@PathVariable String upc, @RequestParam Integer quantityChange) {
        service.updateStockLevelByUpc(upc, quantityChange);
        return ResponseEntity.ok().build();
    }
}

