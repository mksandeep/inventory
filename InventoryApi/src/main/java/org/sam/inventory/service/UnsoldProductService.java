package org.sam.inventory.service;

import org.sam.inventory.dto.UnsoldProductDto;
import org.sam.inventory.entity.Product;
import org.sam.inventory.mapper.ProductMapper;
import org.sam.inventory.repository.ProductRepository;
import org.sam.inventory.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnsoldProductService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ProductMapper productMapper;

    /**
     * Find products not sold for more than specified months
     */
    public List<UnsoldProductDto> findUnsoldProductsForMonths(int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        List<Product> allProducts = productRepository.findAll();
        List<UnsoldProductDto> unsoldProducts = new ArrayList<>();

        for (Product product : allProducts) {
            LocalDate lastSaleDate = saleRepository.findLastSaleDateByProductId(product.getProductId());

            // If no sales or last sale is before cutoff date
            if (lastSaleDate == null || lastSaleDate.isBefore(cutoffDate)) {
                UnsoldProductDto dto = new UnsoldProductDto();
                dto.setProductId(product.getProductId());
                dto.setProductDescription(product.getProductDescription());
                dto.setSku(product.getSku());
                dto.setCategory(product.getCategory());
                dto.setAisleNumber(String.valueOf(product.getAisleNumber()));
                dto.setBayNumber(String.valueOf(product.getBayNumber()));
                dto.setCurrentStockLevel(product.getCurrentStockLevel());
                dto.setUnitCost(product.getUnitCost());
                dto.setRetailPrice(product.getRetailPrice());
                dto.setInventoryValue(product.getInventoryValue());
                dto.setExpiryDate(product.getExpiryDate());

                if (lastSaleDate != null) {
                    long daysSinceSale = ChronoUnit.DAYS.between(lastSaleDate, LocalDate.now());
                    dto.setDaysSinceLastSale(daysSinceSale);
                } else {
                    dto.setDaysSinceLastSale(-1L); // Never sold
                }

                dto.setUnsoldCategory(months == 3 ? "3MONTHS" : months == 6 ? "6MONTHS" : months + "MONTHS");
                unsoldProducts.add(dto);
            }
        }

        return unsoldProducts;
    }

    /**
     * Find products not sold for 3+ months
     */
    public List<UnsoldProductDto> findProductsNotSoldFor3Months() {
        return findUnsoldProductsForMonths(3);
    }

    /**
     * Find products not sold for 6+ months
     */
    public List<UnsoldProductDto> findProductsNotSoldFor6Months() {
        return findUnsoldProductsForMonths(6);
    }

    /**
     * Generate email body for unsold products
     */
    public String generateUnsoldProductsEmailBody(List<UnsoldProductDto> products3Months, List<UnsoldProductDto> products6Months) {
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("INVENTORY REPORT - UNSOLD PRODUCTS\n");
        emailBody.append("=====================================\n");
        emailBody.append("Report Generated: ").append(LocalDate.now()).append("\n\n");

        // 6 Months unsold
        if (!products6Months.isEmpty()) {
            emailBody.append("\n⚠️ CRITICAL - NOT SOLD FOR 6+ MONTHS (").append(products6Months.size()).append(" products)\n");
            emailBody.append("=====================================\n");
            for (UnsoldProductDto product : products6Months) {
                emailBody.append(formatProductLine(product));
            }
            emailBody.append("\nTotal Inventory Value (6+ months unsold): $").append(
                products6Months.stream()
                    .map(p -> p.getInventoryValue() != null ? p.getInventoryValue().doubleValue() : 0)
                    .reduce(0.0, Double::sum)
            ).append("\n");
        }

        // 3 Months unsold
        if (!products3Months.isEmpty()) {
            emailBody.append("\n⚠️ WARNING - NOT SOLD FOR 3+ MONTHS (").append(products3Months.size()).append(" products)\n");
            emailBody.append("=====================================\n");
            for (UnsoldProductDto product : products3Months) {
                emailBody.append(formatProductLine(product));
            }
            emailBody.append("\nTotal Inventory Value (3+ months unsold): $").append(
                products3Months.stream()
                    .map(p -> p.getInventoryValue() != null ? p.getInventoryValue().doubleValue() : 0)
                    .reduce(0.0, Double::sum)
            ).append("\n");
        }

        emailBody.append("\n\nRECOMMENDED ACTIONS:\n");
        emailBody.append("- Review pricing strategy for 6+ month unsold items\n");
        emailBody.append("- Consider promotions or discounts\n");
        emailBody.append("- Plan inventory clearance sales\n");
        emailBody.append("- Check for expired or damaged items\n\n");
        emailBody.append("End of Report\n");

        return emailBody.toString();
    }

    private String formatProductLine(UnsoldProductDto product) {
        return String.format(
            "• %s (SKU: %s)\n  Category: %s | Location: Aisle %s, Bay %s\n  Stock: %d units | Unit Cost: $%.2f | Inventory Value: $%.2f\n  Last Sale: %s days ago\n\n",
            product.getProductDescription(),
            product.getSku(),
            product.getCategory(),
            product.getAisleNumber(),
            product.getBayNumber(),
            product.getCurrentStockLevel(),
            product.getUnitCost() != null ? product.getUnitCost().doubleValue() : 0,
            product.getInventoryValue() != null ? product.getInventoryValue().doubleValue() : 0,
            product.getDaysSinceLastSale() > 0 ? product.getDaysSinceLastSale() : "Never"
        );
    }
}

