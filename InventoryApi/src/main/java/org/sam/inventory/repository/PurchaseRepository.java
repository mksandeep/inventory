package org.sam.inventory.repository;

import org.sam.inventory.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByStockId(Long stockId);
    Optional<Purchase> findByUpc(String upc);
    List<Purchase> findByUpcOrderByCreatedTimestampDesc(String upc);

    @Query("SELECT SUM(p.totalPurchaseAmount) FROM Purchase p WHERE YEAR(p.orderDate) = :year AND QUARTER(p.orderDate) = :quarter")
    BigDecimal getTotalSpendByQuarter(@Param("year") int year, @Param("quarter") int quarter);

    // Total spent by quarter
    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM order_date) AS quarter, SUM(total_purchase_amount) AS value FROM sam.purchases GROUP BY quarter ORDER BY quarter", nativeQuery = true)
    List<Object[]> findTotalSpentByQuarterRaw();

    // Total stock received by quarter
    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM order_date) AS quarter, SUM(units_ordered) AS value FROM sam.purchases GROUP BY quarter ORDER BY quarter", nativeQuery = true)
    List<Object[]> findTotalStockReceivedByQuarterRaw();

    // Date range queries
    @Query("SELECT p FROM Purchase p WHERE p.createdTimestamp BETWEEN :startDate AND :endDate ORDER BY p.createdTimestamp DESC")
    List<Purchase> findPurchasesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
