package org.sam.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SaleDto {
    private Long saleId;
    private String invoiceNo;
    private Long stockId;
    private String upc; // Unique Product Code
    private LocalDate transactionDate;
    private LocalDate shipDate;
    private String customerName;
    private Integer unitsSold;
    private BigDecimal purchasedPrice;
    private BigDecimal salePrice;
    private BigDecimal totalSalesAmount;
    private BigDecimal salesCommission;
    private BigDecimal shippingCost;
    private BigDecimal handlingCost;
    private BigDecimal profit;
    private LocalDate sellDate;
    private String createdBy;
    private LocalDateTime createdTimestamp;
    private String updatedBy;
    private LocalDateTime updatedTimestamp;
}
