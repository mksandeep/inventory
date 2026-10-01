package org.sam.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnsoldProductDto {
    private Long productId;
    private String productDescription;
    private String sku;
    private String category;
    private String aisleNumber;
    private String bayNumber;
    private Integer currentStockLevel;
    private BigDecimal unitCost;
    private BigDecimal retailPrice;
    private BigDecimal inventoryValue;
    private LocalDate expiryDate;
    private Long daysSinceLastSale;  // Days since last sale transaction
    private String unsoldCategory;    // "3MONTHS" or "6MONTHS"
}

