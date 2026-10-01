package org.sam.inventory.controller;

import org.sam.inventory.dto.UnsoldProductDto;
import org.sam.inventory.service.UnsoldProductService;
import org.sam.inventory.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/unsold")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Unsold Products Report", description = "Reports for unsold products")
@Slf4j
public class UnsoldProductReportController {

    @Autowired
    private UnsoldProductService unsoldProductService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/3months")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get products not sold for 3+ months",
               description = "Retrieve list of products not sold for 3 or more months")
    public ResponseEntity<List<UnsoldProductDto>> getProductsNotSoldFor3Months() {
        log.info("Fetching products not sold for 3+ months");
        List<UnsoldProductDto> products = unsoldProductService.findProductsNotSoldFor3Months();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/6months")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get products not sold for 6+ months",
               description = "Retrieve list of products not sold for 6 or more months")
    public ResponseEntity<List<UnsoldProductDto>> getProductsNotSoldFor6Months() {
        log.info("Fetching products not sold for 6+ months");
        List<UnsoldProductDto> products = unsoldProductService.findProductsNotSoldFor6Months();
        return ResponseEntity.ok(products);
    }

    @PostMapping("/send-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send unsold products report via email",
               description = "Generate and send email with unsold products (3+ and 6+ months)")
    public ResponseEntity<Map<String, String>> sendUnsoldProductsEmail(@RequestParam(defaultValue = "m.k.sandeep@gmail.com") String email) {
        log.info("Sending unsold products report to: {}", email);

        try {
            // Get unsold products
            List<UnsoldProductDto> products3Months = unsoldProductService.findProductsNotSoldFor3Months();
            List<UnsoldProductDto> products6Months = unsoldProductService.findProductsNotSoldFor6Months();

            // Generate email body
            String emailBody = unsoldProductService.generateUnsoldProductsEmailBody(products3Months, products6Months);

            // Send email
            String subject = "Inventory Report: Unsold Products (" + products3Months.size() + " items @3mo, " + products6Months.size() + " items @6mo)";
            emailService.sendEmail(email, subject, emailBody);

            Map<String, String> response = new HashMap<>();
            response.put("status", "Email sent successfully");
            response.put("to", email);
            response.put("productsNotSoldFor3Months", String.valueOf(products3Months.size()));
            response.put("productsNotSoldFor6Months", String.valueOf(products6Months.size()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending email: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "Error sending email");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get unsold products summary",
               description = "Get summary statistics of unsold products")
    public ResponseEntity<Map<String, Object>> getUnsoldProductsSummary() {
        log.info("Fetching unsold products summary");

        List<UnsoldProductDto> products3Months = unsoldProductService.findProductsNotSoldFor3Months();
        List<UnsoldProductDto> products6Months = unsoldProductService.findProductsNotSoldFor6Months();

        Map<String, Object> summary = new HashMap<>();
        summary.put("productsNotSoldFor3Months", products3Months.size());
        summary.put("productsNotSoldFor6Months", products6Months.size());

        double inventoryValue3Months = products3Months.stream()
            .mapToDouble(p -> p.getInventoryValue() != null ? p.getInventoryValue().doubleValue() : 0)
            .sum();

        double inventoryValue6Months = products6Months.stream()
            .mapToDouble(p -> p.getInventoryValue() != null ? p.getInventoryValue().doubleValue() : 0)
            .sum();

        summary.put("inventoryValue3Months", inventoryValue3Months);
        summary.put("inventoryValue6Months", inventoryValue6Months);
        summary.put("totalUnsoldValue", inventoryValue3Months + inventoryValue6Months);

        return ResponseEntity.ok(summary);
    }
}

