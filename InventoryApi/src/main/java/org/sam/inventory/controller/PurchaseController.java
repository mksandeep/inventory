package org.sam.inventory.controller;

import org.sam.inventory.dto.PurchaseDto;
import org.sam.inventory.dto.BulkUploadResponse;
import org.sam.inventory.service.PurchaseService;
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
@RequestMapping("/api/purchases")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ExcelUploadService excelUploadService;

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @PostMapping
    public ResponseEntity<PurchaseDto> create(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(purchaseService.createPurchase(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDto> update(@PathVariable Long id, @RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(purchaseService.updatePurchase(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            return ResponseEntity.ok(purchaseService.getPurchasesByDateRange(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getAll() {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }

    @GetMapping("/total-spent-by-quarter")
    public ResponseEntity<List<Map<String, Object>>> getTotalSpentByQuarter() {
        return ResponseEntity.ok(purchaseService.getTotalSpentByQuarter());
    }

    @GetMapping("/stock-received-by-quarter")
    public ResponseEntity<List<Map<String, Object>>> getTotalStockReceivedByQuarter() {
        return ResponseEntity.ok(purchaseService.getTotalStockReceivedByQuarter());
    }

    @PostMapping("/upload")
    public ResponseEntity<BulkUploadResponse> uploadPurchases(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(excelUploadService.uploadPurchases(file));
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] template = excelTemplateService.generatePurchasesTemplate();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=purchases_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

