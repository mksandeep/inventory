package org.sam.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long itemId;
    private String itemDescription;
    private String sku;
    private String dimensions;
    private String category;
    private Integer aisleNumber;
    private Integer bayNumber;
    private Integer startingInventory;
    private Integer lowStockThreshold;
    private BigDecimal unitCost;
    private BigDecimal markup;
    private BigDecimal retailPrice;
    private Integer currentStockLevel;
    private BigDecimal inventoryValue;
    private LocalDate expiryDate;
}
