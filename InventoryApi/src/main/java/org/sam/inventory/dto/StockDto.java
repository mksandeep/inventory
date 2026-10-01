package org.sam.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {
    private Long stockId;
    private String productDescription;
    private String upc; // Unique Product Code
    private String sku;
    private String dimensions;
    private String category;
    private String aisleNumber;
    private String bayNumber;
    private Integer startingInventory;
    private Integer lowStockThreshold;
    private BigDecimal unitCost;
    private BigDecimal markup;
    private BigDecimal retailPrice;
    private Integer currentStockLevel;
    private BigDecimal inventoryValue;
    private LocalDate expiryDate;
    private Integer unitsSold;
    private String createdBy;
    private LocalDateTime createdTimestamp;
    private String updatedBy;
    private LocalDateTime updatedTimestamp;
}

