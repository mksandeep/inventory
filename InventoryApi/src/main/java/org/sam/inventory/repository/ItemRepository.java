package org.sam.inventory.repository;

import org.sam.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySku(String sku);
    List<Item> findByCategory(String category);
    List<Item> findByAisleNumberAndBayNumber(Integer aisle, Integer bay);
    @Query("SELECT i FROM Item i WHERE i.currentStockLevel <= i.lowStockThreshold")
    List<Item> findLowStockItems();
    @Query("SELECT i FROM Item i WHERE i.expiryDate < :date")
    List<Item> findExpiredItems(@Param("date") LocalDate date);
    @Query("SELECT i FROM Item i WHERE i.expiryDate BETWEEN :now AND :future")
    List<Item> findItemsExpiringWithin(@Param("now") LocalDate now, @Param("future") LocalDate future);
}
