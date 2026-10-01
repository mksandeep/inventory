package org.sam.inventory.service;

import org.sam.inventory.dto.PurchaseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PurchaseService {
    PurchaseDto createPurchase(PurchaseDto dto);
    PurchaseDto updatePurchase(Long id, PurchaseDto dto);
    void deletePurchase(Long id);
    PurchaseDto getPurchaseById(Long id);
    List<PurchaseDto> getAllPurchases();
    List<Map<String, Object>> getTotalSpentByQuarter();
    List<Map<String, Object>> getTotalStockReceivedByQuarter();
    List<PurchaseDto> getPurchasesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}

