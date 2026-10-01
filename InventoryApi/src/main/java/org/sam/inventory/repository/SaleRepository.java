package org.sam.inventory.repository;

import org.sam.inventory.entity.Sale;
import org.sam.inventory.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByStockId(Long stockId);
    Optional<Sale> findByUpc(String upc);
    List<Sale> findByUpcOrderByCreatedTimestampDesc(String upc);

    // Aliases for backward compatibility - Product and Stock use same entity schema
    default List<Sale> findByProductId(Long productId) {
        return findByStockId(productId);
    }

    default LocalDate findLastSaleDateByProductId(Long productId) {
        return findLastSaleDateByStockId(productId);
    }

    @Query("SELECT SUM(s.totalSalesAmount) FROM Sale s WHERE YEAR(s.transactionDate) = :year AND QUARTER(s.transactionDate) = :quarter")
    BigDecimal getTotalRevenueByQuarter(@Param("year") int year, @Param("quarter") int quarter);

    @Query("SELECT s.stockId, SUM(s.unitsSold) as totalSold FROM Sale s WHERE YEAR(s.transactionDate) = :year AND QUARTER(s.transactionDate) = :quarter GROUP BY s.stockId ORDER BY totalSold DESC LIMIT 5")
    List<Object[]> getTop5ProductsSoldByQuarter(@Param("year") int year, @Param("quarter") int quarter);

    // Top 5 products sold per quarter (returns List<Map<String,Object>>)
    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM transaction_date) AS quarter, product_id, SUM(units_sold) AS total_sold FROM sam.sales GROUP BY EXTRACT(QUARTER FROM transaction_date), product_id ORDER BY EXTRACT(QUARTER FROM transaction_date), total_sold DESC", nativeQuery = true)
    List<Object[]> findTop5ItemsSoldPerQuarterRaw();

    // Total sales revenue by quarter
    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM transaction_date) AS quarter, SUM(total_sales_amount) AS value FROM sam.sales GROUP BY EXTRACT(QUARTER FROM transaction_date) ORDER BY EXTRACT(QUARTER FROM transaction_date)", nativeQuery = true)
    List<Object[]> findTotalSalesRevenueByQuarterRaw();

    // Total units sold by quarter
    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM transaction_date) AS quarter, SUM(units_sold) AS value FROM sam.sales GROUP BY EXTRACT(QUARTER FROM transaction_date) ORDER BY EXTRACT(QUARTER FROM transaction_date)", nativeQuery = true)
    List<Object[]> findTotalUnitsSoldByQuarterRaw();

    // Revenue by customer per quarter
    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM transaction_date) AS quarter, customer_name, SUM(total_sales_amount) AS value FROM sam.sales GROUP BY EXTRACT(QUARTER FROM transaction_date), customer_name ORDER BY EXTRACT(QUARTER FROM transaction_date), customer_name", nativeQuery = true)
    List<Object[]> findRevenueByCustomerPerQuarterRaw();

    // Find stocks not sold in 3 months
    @Query(value = "SELECT s.* FROM sam.products s WHERE s.product_id NOT IN (SELECT DISTINCT product_id FROM sam.sales WHERE transaction_date > NOW() - INTERVAL '3 months')", nativeQuery = true)
    List<Stock> findStocksNotSoldInMonths(@Param("days") int days);

    // Get last sale date for a stock
    @Query(value = "SELECT MAX(transaction_date) FROM sam.sales WHERE stock_id = :stockId", nativeQuery = true)
    LocalDate findLastSaleDateByStockId(@Param("stockId") Long stockId);

    // Date range queries
    @Query("SELECT s FROM Sale s WHERE s.createdTimestamp BETWEEN :startDate AND :endDate ORDER BY s.createdTimestamp DESC")
    List<Sale> findSalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
