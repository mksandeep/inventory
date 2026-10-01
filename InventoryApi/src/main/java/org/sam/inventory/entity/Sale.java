package org.sam.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sales", schema = "sam")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleId;

    @NotBlank
    private String invoiceNo;

    @NotNull
    @Column(name = "product_id")
    private Long stockId; // FK to Stock (mapped to product_id for DB compatibility)

    @NotBlank
    @Column(nullable = false)
    private String upc; // Unique Product Code (denormalized for tracking)

    @NotNull
    private LocalDate transactionDate;

    private LocalDate shipDate;

    @NotBlank
    private String customerName;

    @NotNull
    @Min(1)
    private Integer unitsSold;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal purchasedPrice; // Price at which item was purchased

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salePrice; // Price at which item is sold

    @DecimalMin("0.0")
    private BigDecimal totalSalesAmount; // Auto-calculated

    @DecimalMin("0.0")
    private BigDecimal salesCommission; // Commission on sale

    @DecimalMin("0.0")
    private BigDecimal shippingCost; // Shipping cost

    @DecimalMin("0.0")
    private BigDecimal handlingCost; // Handling cost

    @DecimalMin("0.0")
    private BigDecimal profit; // Auto-calculated: (salePrice - purchasedPrice - commission - shipping - handling) * unitsSold

    private LocalDate sellDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;

    @PrePersist
    private void prePersist() {
        calculateTotals();
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
        calculateTotals();
        updatedTimestamp = LocalDateTime.now();
    }

    private void calculateTotals() {
        if (unitsSold != null && salePrice != null) {
            totalSalesAmount = salePrice.multiply(BigDecimal.valueOf(unitsSold));
        }

        // Calculate profit: (salePrice - purchasedPrice - commission - shipping - handling) * unitsSold
        if (unitsSold != null && salePrice != null && purchasedPrice != null) {
            BigDecimal profitPerUnit = salePrice.subtract(purchasedPrice);
            if (salesCommission != null) {
                profitPerUnit = profitPerUnit.subtract(salesCommission);
            }
            if (shippingCost != null) {
                profitPerUnit = profitPerUnit.subtract(shippingCost);
            }
            if (handlingCost != null) {
                profitPerUnit = profitPerUnit.subtract(handlingCost);
            }
            profit = profitPerUnit.multiply(BigDecimal.valueOf(unitsSold));
        }
    }
}
