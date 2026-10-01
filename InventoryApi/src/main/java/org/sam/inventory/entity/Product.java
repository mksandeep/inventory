package org.sam.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "products", schema = "sam", uniqueConstraints = @UniqueConstraint(columnNames = "upc"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    private String productDescription;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String upc; // Unique Product Code

    @NotBlank
    @Column(unique = true)
    private String sku;

    private String dimensions;

    @NotBlank
    private String category;

    private Integer aisleNumber;

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

    @Min(0)
    private Integer currentStockLevel;

    @DecimalMin("0.0")
    private BigDecimal inventoryValue; // Auto-calculated

    private LocalDate expiryDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;

    @PrePersist
    private void calculateFields() {
        if (unitCost != null && markup != null) {
            retailPrice = unitCost.add(unitCost.multiply(markup.divide(BigDecimal.valueOf(100))));
        }
        if (currentStockLevel != null && unitCost != null) {
            inventoryValue = unitCost.multiply(BigDecimal.valueOf(currentStockLevel));
        }
        if (createdTimestamp == null) {
            createdTimestamp = LocalDateTime.now();
        }
        if (updatedBy == null && createdBy != null) {
            updatedBy = createdBy;
        }
        updatedTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        calculateFields();
        updatedTimestamp = LocalDateTime.now();
    }
}

