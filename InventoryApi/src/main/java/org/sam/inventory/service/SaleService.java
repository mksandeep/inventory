package org.sam.inventory.service;

import org.sam.inventory.dto.SaleDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SaleService {
    SaleDto createSale(SaleDto dto);
    SaleDto updateSale(Long id, SaleDto dto);
    void deleteSale(Long id);
    SaleDto getSaleById(Long id);
    List<SaleDto> getAllSales();
    List<Map<String, Object>> getTop5ItemsSoldPerQuarter();
    List<Map<String, Object>> getTotalSalesRevenueByQuarter();
    List<Map<String, Object>> getTotalUnitsSoldByQuarter();
    Map<String, List<Map<String, Object>>> getRevenueByCustomerPerQuarter();
    List<SaleDto> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}

