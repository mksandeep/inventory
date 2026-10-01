package org.sam.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "purchases", schema = "sam")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @NotBlank
    private String orderReferenceNo;

    @NotNull
    @Column(name = "product_id")
    private Long stockId; // FK to Stock (mapped to product_id for DB compatibility)

    @NotBlank
    @Column(nullable = false)
    private String upc; // Unique Product Code (denormalized for tracking)

    @NotNull
    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    @NotBlank
    private String vendor;

    @NotNull
    @Min(1)
    private Integer unitsOrdered;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal costPerUnit;

    @DecimalMin("0.0")
    private BigDecimal totalPurchaseAmount; // Auto-calculated

    @NotNull
    private Integer aisleNumber;

    @NotNull
    private Integer bayNumber;

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
        calculateTotal();
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
        calculateTotal();
        updatedTimestamp = LocalDateTime.now();
    }

    private void calculateTotal() {
        if (unitsOrdered != null && costPerUnit != null) {
            totalPurchaseAmount = costPerUnit.multiply(BigDecimal.valueOf(unitsOrdered));
        }
    }
}
