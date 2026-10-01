package org.sam.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "items", schema = "sam")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @NotBlank
    private String itemDescription;

    @NotBlank
    @Column(unique = true)
    private String sku;

    private String dimensions;

    @NotBlank
    private String category;

    @NotNull
    private Integer aisleNumber;

    @NotNull
    private Integer bayNumber;

    @NotNull
    @Min(0)
    private Integer startingInventory;

    @NotNull
    @Min(0)
    private Integer lowStockThreshold;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal unitCost;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal markup;

    @DecimalMin("0.0")
    private BigDecimal retailPrice; // Auto-calculated

    @NotNull
    @Min(0)
    private Integer currentStockLevel;

    @DecimalMin("0.0")
    private BigDecimal inventoryValue; // Auto-calculated

    private LocalDate expiryDate;

    @PrePersist
    @PreUpdate
    private void calculateFields() {
        if (unitCost != null && markup != null) {
            retailPrice = unitCost.add(unitCost.multiply(markup.divide(BigDecimal.valueOf(100))));
        }
        if (currentStockLevel != null && unitCost != null) {
            inventoryValue = unitCost.multiply(BigDecimal.valueOf(currentStockLevel));
        }
    }
}
