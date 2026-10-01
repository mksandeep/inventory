package org.sam.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseDto {
    private Long purchaseId;
    private String orderReferenceNo;
    private Long stockId;
    private String upc; // Unique Product Code
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private String vendor;
    private Integer unitsOrdered;
    private BigDecimal costPerUnit;
    private BigDecimal totalPurchaseAmount;
    private Integer aisleNumber;
    private Integer bayNumber;
    private String createdBy;
    private LocalDateTime createdTimestamp;
    private String updatedBy;
    private LocalDateTime updatedTimestamp;
}
