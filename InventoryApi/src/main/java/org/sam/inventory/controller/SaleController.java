package org.sam.inventory.controller;

import org.sam.inventory.dto.SaleDto;
import org.sam.inventory.dto.BulkUploadResponse;
import org.sam.inventory.service.SaleService;
import org.sam.inventory.service.ExcelUploadService;
import org.sam.inventory.service.ExcelTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @PostMapping
    public ResponseEntity<SaleDto> create(@RequestBody SaleDto dto) {
        return ResponseEntity.ok(saleService.createSale(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDto> update(@PathVariable Long id, @RequestBody SaleDto dto) {
        return ResponseEntity.ok(saleService.updateSale(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<SaleDto>> getSalesByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            return ResponseEntity.ok(saleService.getSalesByDateRange(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> getAll() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/top5-products-per-quarter")
    public ResponseEntity<List<Map<String, Object>>> getTop5ProductsPerQuarter() {
        return ResponseEntity.ok(saleService.getTop5ItemsSoldPerQuarter());
    }

    @GetMapping("/revenue-by-quarter")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByQuarter() {
        return ResponseEntity.ok(saleService.getTotalSalesRevenueByQuarter());
    }

    @GetMapping("/units-sold-by-quarter")
    public ResponseEntity<List<Map<String, Object>>> getUnitsSoldByQuarter() {
        return ResponseEntity.ok(saleService.getTotalUnitsSoldByQuarter());
    }

    @GetMapping("/revenue-by-customer-per-quarter")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getRevenueByCustomerPerQuarter() {
        return ResponseEntity.ok(saleService.getRevenueByCustomerPerQuarter());
    }

    @PostMapping("/upload")
    public ResponseEntity<BulkUploadResponse> uploadSales(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(excelUploadService.uploadSales(file));
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] template = excelTemplateService.generateSalesTemplate();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

