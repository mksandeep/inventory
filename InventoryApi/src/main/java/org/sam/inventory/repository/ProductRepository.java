package org.sam.inventory.repository;

import org.sam.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByAisleNumberAndBayNumber(Integer aisle, Integer bay);
    @Query("SELECT p FROM Product p WHERE p.currentStockLevel <= p.lowStockThreshold")
    List<Product> findLowStockProducts();
    @Query("SELECT p FROM Product p WHERE p.expiryDate < :date")
    List<Product> findExpiredProducts(@Param("date") LocalDate date);
    @Query("SELECT p FROM Product p WHERE p.expiryDate BETWEEN :now AND :future")
    List<Product> findProductsExpiringWithin(@Param("now") LocalDate now, @Param("future") LocalDate future);

    // Date range queries
    @Query("SELECT p FROM Product p WHERE p.createdTimestamp BETWEEN :startDate AND :endDate ORDER BY p.createdTimestamp DESC")
    List<Product> findProductsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Product p WHERE p.updatedTimestamp BETWEEN :startDate AND :endDate ORDER BY p.updatedTimestamp DESC")
    List<Product> findProductsByUpdateDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
