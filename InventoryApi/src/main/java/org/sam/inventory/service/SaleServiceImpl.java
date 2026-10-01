package org.sam.inventory.service;

import org.sam.inventory.dto.SaleDto;
import org.sam.inventory.entity.Stock;
import org.sam.inventory.entity.Sale;
import org.sam.inventory.mapper.SaleMapper;
import org.sam.inventory.repository.StockRepository;
import org.sam.inventory.repository.SaleRepository;
import org.sam.inventory.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private SaleMapper saleMapper;

    @Override
    public SaleDto createSale(SaleDto dto) {
        Sale sale = saleMapper.toEntity(dto);
        String currentUser = SecurityUtil.getCurrentUserEmail();
        sale.setCreatedBy(currentUser);
        sale.setUpdatedBy(currentUser);
        sale.setCreatedTimestamp(LocalDateTime.now());
        sale.setUpdatedTimestamp(LocalDateTime.now());

        // Automatically deduct from stock based on UPC
        if (dto.getUpc() != null) {
            Stock stock = stockRepository.findByUpc(dto.getUpc())
                    .orElseThrow(() -> new RuntimeException("Stock with UPC " + dto.getUpc() + " not found"));
            Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
            if (currentLevel < dto.getUnitsSold()) {
                throw new RuntimeException("Insufficient stock. Available: " + currentLevel + ", Requested: " + dto.getUnitsSold());
            }
            stock.setCurrentStockLevel(currentLevel - dto.getUnitsSold());
            stock.setUpdatedBy(currentUser);
            stock.setUpdatedTimestamp(LocalDateTime.now());
            stockRepository.save(stock);
        }

        return saleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    public SaleDto updateSale(Long id, SaleDto dto) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found"));

        // Handle stock adjustment if units sold changed
        Integer previousUnits = sale.getUnitsSold() != null ? sale.getUnitsSold() : 0;
        Integer newUnits = dto.getUnitsSold() != null ? dto.getUnitsSold() : 0;
        Integer unitsDifference = newUnits - previousUnits;

        sale.setInvoiceNo(dto.getInvoiceNo());
        sale.setStockId(dto.getStockId());
        sale.setUpc(dto.getUpc());
        sale.setTransactionDate(dto.getTransactionDate());
        sale.setShipDate(dto.getShipDate());
        sale.setCustomerName(dto.getCustomerName());
        sale.setUnitsSold(dto.getUnitsSold());
        sale.setPurchasedPrice(dto.getPurchasedPrice());
        sale.setSalePrice(dto.getSalePrice());
        sale.setSalesCommission(dto.getSalesCommission());
        sale.setShippingCost(dto.getShippingCost());
        sale.setHandlingCost(dto.getHandlingCost());
        sale.setSellDate(dto.getSellDate());
        sale.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
        sale.setUpdatedTimestamp(LocalDateTime.now());

        // Update stock if units changed
        if (unitsDifference != 0 && dto.getUpc() != null) {
            Stock stock = stockRepository.findByUpc(dto.getUpc())
                    .orElseThrow(() -> new RuntimeException("Stock with UPC " + dto.getUpc() + " not found"));
            Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
            if (unitsDifference > 0 && currentLevel < unitsDifference) {
                throw new RuntimeException("Insufficient stock for adjustment");
            }
            stock.setCurrentStockLevel(currentLevel - unitsDifference);
            stock.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
            stock.setUpdatedTimestamp(LocalDateTime.now());
            stockRepository.save(stock);
        }

        return saleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found"));

        // Revert stock adjustment when deleting sale
        if (sale.getUpc() != null && sale.getUnitsSold() != null) {
            Stock stock = stockRepository.findByUpc(sale.getUpc()).orElse(null);
            if (stock != null) {
                Integer currentLevel = stock.getCurrentStockLevel() != null ? stock.getCurrentStockLevel() : 0;
                stock.setCurrentStockLevel(currentLevel + sale.getUnitsSold());
                stock.setUpdatedBy(SecurityUtil.getCurrentUserEmail());
                stock.setUpdatedTimestamp(LocalDateTime.now());
                stockRepository.save(stock);
            }
        }

        saleRepository.deleteById(id);
    }

    @Override
    public SaleDto getSaleById(Long id) {
        return saleMapper.toDto(saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found")));
    }

    @Override
    public List<SaleDto> getAllSales() {
        return saleRepository.findAll().stream().map(saleMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTop5ItemsSoldPerQuarter() {
        List<Object[]> raw = saleRepository.findTop5ItemsSoldPerQuarterRaw();
        Map<String, List<Object[]>> grouped = new LinkedHashMap<>();
        for (Object[] row : raw) {
            String quarter = (String) row[0];
            grouped.computeIfAbsent(quarter, k -> new ArrayList<>()).add(row);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Object[]>> entry : grouped.entrySet()) {
            List<Object[]> rows = entry.getValue();
            rows.sort((a, b) -> ((Long) b[2]).compareTo((Long) a[2]));
            List<String> products = new ArrayList<>();
            for (int i = 0; i < Math.min(5, rows.size()); i++) {
                Long stockId = ((Number) rows.get(i)[1]).longValue();
                Optional<Stock> stock = stockRepository.findById(stockId);
                products.add(stock.map(Stock::getProductDescription).orElse("Stock " + stockId));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("quarter", entry.getKey());
            map.put("items", products);
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTotalSalesRevenueByQuarter() {
        List<Object[]> raw = saleRepository.findTotalSalesRevenueByQuarterRaw();
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
    public List<Map<String, Object>> getTotalUnitsSoldByQuarter() {
        List<Object[]> raw = saleRepository.findTotalUnitsSoldByQuarterRaw();
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
    public Map<String, List<Map<String, Object>>> getRevenueByCustomerPerQuarter() {
        List<Object[]> raw = saleRepository.findRevenueByCustomerPerQuarterRaw();
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        for (Object[] row : raw) {
            String quarter = (String) row[0];
            String customer = (String) row[1];
            Number value = (Number) row[2];
            Map<String, Object> map = new HashMap<>();
            map.put("customer", customer);
            map.put("value", value);
            result.computeIfAbsent(quarter, k -> new ArrayList<>()).add(map);
        }
        return result;
    }

    @Override
    public List<SaleDto> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findSalesByDateRange(startDate, endDate).stream()
                .map(saleMapper::toDto)
                .collect(Collectors.toList());
    }
}
