package org.sam.inventory.controller;

import org.sam.inventory.dto.ProductDto;
import org.sam.inventory.dto.BulkUploadResponse;
import org.sam.inventory.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
        return ResponseEntity.ok(service.createProduct(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(service.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<ProductDto>> getProductsByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate + "T00:00:00");
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate + "T23:59:59");
            return ResponseEntity.ok(service.getProductsByDateRange(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(@RequestParam(required = false) String sku,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String aisle,
                                                  @RequestParam(required = false) String bay) {
        if (sku != null) return ResponseEntity.ok(service.searchBySku(sku));
        if (category != null) return ResponseEntity.ok(service.searchByCategory(category));
        if (aisle != null && bay != null) return ResponseEntity.ok(service.searchByAisleBay(aisle, bay));
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStock() {
        return ResponseEntity.ok(service.getLowStockProducts());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<ProductDto>> getExpired() {
        return ResponseEntity.ok(service.getExpiredProducts());
    }

    @GetMapping("/expiring/{days}")
    public ResponseEntity<List<ProductDto>> getExpiring(@PathVariable int days) {
        return ResponseEntity.ok(service.getProductsExpiringWithin(days));
    }

    @GetMapping("/total-value")
    public ResponseEntity<BigDecimal> getTotalValue() {
        return ResponseEntity.ok(service.getTotalInventoryValue());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BulkUploadResponse> uploadProducts(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(excelUploadService.uploadProducts(file));
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] template = excelTemplateService.generateProductTemplate();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
