package org.sam.inventory.service;

import org.sam.inventory.dto.PurchaseDto;
import org.sam.inventory.entity.Purchase;
import org.sam.inventory.entity.Stock;
import org.sam.inventory.mapper.PurchaseMapper;
import org.sam.inventory.repository.PurchaseRepository;
import org.sam.inventory.repository.StockRepository;
import org.sam.inventory.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private StockRepository stockRepository;

    @Override
    public PurchaseDto createPurchase(PurchaseDto dto) {
        Purchase purchase = purchaseMapper.toEntity(dto);
        String currentUser = SecurityUtil.getCurrentUserEmail();
        purchase.setCreatedBy(currentUser);
        purchase.setUpdatedBy(currentUser);
        purchase.setCreatedTimestamp(LocalDateTime.now());
        purchase.setUpdatedTimestamp(LocalDateTime.now());

        // Automatically update stock with purchased quantity
        if (dto.getUpc() != null) {
            Stock stock = stockRepository.findByUpc(dto.getUpc())
                    .orElseThrow(() -> new RuntimeException("Stock with UPC " + dto.getUpc() + " not found"));
            Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
            stock.setCurrentStockLevel(currentLevel + dto.getUnitsOrdered());
            stock.setUpdatedBy(currentUser);
            stock.setUpdatedTimestamp(LocalDateTime.now());
            stockRepository.save(stock);
        }

        return purchaseMapper.toDto(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseDto updatePurchase(Long id, PurchaseDto dto) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));

        // Handle stock adjustment if units ordered changed
        Integer previousUnits = purchase.getUnitsOrdered() != null ? purchase.getUnitsOrdered() : 0;
        Integer newUnits = dto.getUnitsOrdered() != null ? dto.getUnitsOrdered() : 0;
        Integer unitsDifference = newUnits - previousUnits;

        purchase.setOrderReferenceNo(dto.getOrderReferenceNo());
        purchase.setStockId(dto.getStockId());
        purchase.setUpc(dto.getUpc());
        purchase.setOrderDate(dto.getOrderDate());
        purchase.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        purchase.setVendor(dto.getVendor());
        purchase.setUnitsOrdered(dto.getUnitsOrdered());
        purchase.setCostPerUnit(dto.getCostPerUnit());
        purchase.setAisleNumber(dto.getAisleNumber());
        purchase.setBayNumber(dto.getBayNumber());
        purchase.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
        purchase.setUpdatedTimestamp(LocalDateTime.now());

        // Update stock if units changed
        if (unitsDifference != 0 && dto.getUpc() != null) {
            Stock stock = stockRepository.findByUpc(dto.getUpc())
                    .orElseThrow(() -> new RuntimeException("Stock with UPC " + dto.getUpc() + " not found"));
            Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
            stock.setCurrentStockLevel(currentLevel + unitsDifference);
            stock.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
            stock.setUpdatedTimestamp(LocalDateTime.now());
            stockRepository.save(stock);
        }

        return purchaseMapper.toDto(purchaseRepository.save(purchase));
    }

    @Override
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));

        // Revert stock adjustment when deleting purchase
        if (purchase.getUpc() != null && purchase.getUnitsOrdered() != null) {
            Stock stock = stockRepository.findByUpc(purchase.getUpc()).orElse(null);
            if (stock != null) {
                Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
                stock.setCurrentStockLevel(Math.max(0, currentLevel - purchase.getUnitsOrdered()));
                stock.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
                stock.setUpdatedTimestamp(LocalDateTime.now());
                stockRepository.save(stock);
            }
        }

        purchaseRepository.deleteById(id);
    }

    @Override
    public PurchaseDto getPurchaseById(Long id) {
        return purchaseMapper.toDto(purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found")));
    }

    @Override
    public List<PurchaseDto> getAllPurchases() {
        return purchaseRepository.findAll().stream().map(purchaseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTotalSpentByQuarter() {
        List<Object[]> raw = purchaseRepository.findTotalSpentByQuarterRaw();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("quarter", row[0]);
            map.put("value", row[1]);
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTotalStockReceivedByQuarter() {
        List<Object[]> raw = purchaseRepository.findTotalStockReceivedByQuarterRaw();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("quarter", row[0]);
            map.put("value", row[1]);
            result.add(map);
        }
        return result;
    }

    @Override
    public List<PurchaseDto> getPurchasesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseRepository.findPurchasesByDateRange(startDate, endDate).stream()
                .map(purchaseMapper::toDto)
                .collect(Collectors.toList());
    }
}
