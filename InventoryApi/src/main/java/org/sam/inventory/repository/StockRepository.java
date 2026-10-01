package org.sam.inventory.repository;

import org.sam.inventory.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findBySku(String sku);
    List<Stock> findByCategory(String category);
    List<Stock> findByAisleNumberAndBayNumber(Integer aisle, Integer bay);
    Optional<Stock> findByUpc(String upc);

    @Query("SELECT s FROM Stock s WHERE s.currentStockLevel <= s.lowStockThreshold")
    List<Stock> findLowStockProducts();

    @Query("SELECT s FROM Stock s WHERE s.expiryDate < :date")
    List<Stock> findExpiredProducts(@Param("date") LocalDate date);

    @Query("SELECT s FROM Stock s WHERE s.expiryDate BETWEEN :now AND :future")
    List<Stock> findProductsExpiringWithin(@Param("now") LocalDate now, @Param("future") LocalDate future);

    // Date range queries
    @Query("SELECT s FROM Stock s WHERE s.createdTimestamp BETWEEN :startDate AND :endDate ORDER BY s.createdTimestamp DESC")
    List<Stock> findStockByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Stock s WHERE s.updatedTimestamp BETWEEN :startDate AND :endDate ORDER BY s.updatedTimestamp DESC")
    List<Stock> findStockByUpdateDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

