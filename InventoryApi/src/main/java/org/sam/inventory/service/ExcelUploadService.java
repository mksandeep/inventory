package org.sam.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sam.inventory.dto.BulkUploadResponse;
import org.sam.inventory.entity.Product;
import org.sam.inventory.entity.Purchase;
import org.sam.inventory.entity.Sale;
import org.sam.inventory.repository.ProductRepository;
import org.sam.inventory.repository.PurchaseRepository;
import org.sam.inventory.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ExcelUploadService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BulkUploadResponse uploadProducts(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            for (Row row : sheet) {
                rowNum++;
                if (rowNum == 1) continue; // Skip header

                try {
                    Product product = parseProductRow(row);
                    if (product != null) {
                        productRepository.save(product);
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }

            String message = String.format("Successfully imported %d products", successCount);
            if (failureCount > 0) {
                message += String.format(". %d rows failed.", failureCount);
            }

            return BulkUploadResponse.builder()
                    .successCount(successCount)
                    .failureCount(failureCount)
                    .message(message)
                    .errors(errors)
                    .build();

        } catch (Exception e) {
            log.error("Error uploading products", e);
            return BulkUploadResponse.builder()
                    .successCount(0)
                    .failureCount(0)
                    .message("Failed to process file: " + e.getMessage())
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    public BulkUploadResponse uploadSales(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            for (Row row : sheet) {
                rowNum++;
                if (rowNum == 1) continue; // Skip header

                try {
                    Sale sale = parseSaleRow(row);
                    if (sale != null) {
                        saleRepository.save(sale);
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }

            String message = String.format("Successfully imported %d sales", successCount);
            if (failureCount > 0) {
                message += String.format(". %d rows failed.", failureCount);
            }

            return BulkUploadResponse.builder()
                    .successCount(successCount)
                    .failureCount(failureCount)
                    .message(message)
                    .errors(errors)
                    .build();

        } catch (Exception e) {
            log.error("Error uploading sales", e);
            return BulkUploadResponse.builder()
                    .successCount(0)
                    .failureCount(0)
                    .message("Failed to process file: " + e.getMessage())
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    public BulkUploadResponse uploadPurchases(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            for (Row row : sheet) {
                rowNum++;
                if (rowNum == 1) continue; // Skip header

                try {
                    Purchase purchase = parsePurchaseRow(row);
                    if (purchase != null) {
                        purchaseRepository.save(purchase);
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }

            String message = String.format("Successfully imported %d purchases", successCount);
            if (failureCount > 0) {
                message += String.format(". %d rows failed.", failureCount);
            }

            return BulkUploadResponse.builder()
                    .successCount(successCount)
                    .failureCount(failureCount)
                    .message(message)
                    .errors(errors)
                    .build();

        } catch (Exception e) {
            log.error("Error uploading purchases", e);
            return BulkUploadResponse.builder()
                    .successCount(0)
                    .failureCount(0)
                    .message("Failed to process file: " + e.getMessage())
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // Alias for backward compatibility: uploadStocks -> uploadProducts
    public BulkUploadResponse uploadStocks(MultipartFile file) {
        return uploadProducts(file);
    }

    private Product parseProductRow(Row row) {
        if (row == null || row.getPhysicalNumberOfCells() < 11) {
            throw new IllegalArgumentException("Invalid row format");
        }

        Product product = new Product();
        product.setProductDescription(getCellStringValue(row.getCell(0)));
        product.setSku(getCellStringValue(row.getCell(1)));
        product.setDimensions(getCellStringValue(row.getCell(2)));
        product.setCategory(getCellStringValue(row.getCell(3)));
        product.setAisleNumber((int) getCellNumericValue(row.getCell(4)));
        product.setBayNumber((int) getCellNumericValue(row.getCell(5)));
        product.setStartingInventory((int) getCellNumericValue(row.getCell(6)));
        product.setLowStockThreshold((int) getCellNumericValue(row.getCell(7)));
        product.setUnitCost(BigDecimal.valueOf(getCellNumericValue(row.getCell(8))));
        product.setMarkup(BigDecimal.valueOf(getCellNumericValue(row.getCell(9))));
        product.setCurrentStockLevel((int) getCellNumericValue(row.getCell(10)));

        if (row.getCell(11) != null) {
            product.setExpiryDate(getCellDateValue(row.getCell(11)));
        }

        return product;
    }

    private Sale parseSaleRow(Row row) {
        if (row == null || row.getPhysicalNumberOfCells() < 11) {
            throw new IllegalArgumentException("Invalid row format");
        }

        Sale sale = new Sale();
        sale.setInvoiceNo(getCellStringValue(row.getCell(0)));
        sale.setStockId((long) getCellNumericValue(row.getCell(1)));
        sale.setUpc(getCellStringValue(row.getCell(2)));
        sale.setTransactionDate(getCellDateValue(row.getCell(3)));
        sale.setShipDate(getCellDateValue(row.getCell(4)));
        sale.setCustomerName(getCellStringValue(row.getCell(5)));
        sale.setUnitsSold((int) getCellNumericValue(row.getCell(6)));
        sale.setPurchasedPrice(BigDecimal.valueOf(getCellNumericValue(row.getCell(7))));
        sale.setSalePrice(BigDecimal.valueOf(getCellNumericValue(row.getCell(8))));
        sale.setSalesCommission(BigDecimal.valueOf(getCellNumericValue(row.getCell(9))));
        sale.setShippingCost(BigDecimal.valueOf(getCellNumericValue(row.getCell(10))));
        sale.setHandlingCost(BigDecimal.valueOf(getCellNumericValue(row.getCell(11))));

        if (row.getCell(12) != null && row.getCell(12).getCellType() != CellType.BLANK) {
            sale.setSellDate(getCellDateValue(row.getCell(12)));
        }

        return sale;
    }

    private Purchase parsePurchaseRow(Row row) {
        if (row == null || row.getPhysicalNumberOfCells() < 9) {
            throw new IllegalArgumentException("Invalid row format");
        }

        Purchase purchase = new Purchase();
        purchase.setOrderReferenceNo(getCellStringValue(row.getCell(0)));
        purchase.setStockId((long) getCellNumericValue(row.getCell(1)));
        purchase.setOrderDate(getCellDateValue(row.getCell(2)));
        purchase.setExpectedDeliveryDate(getCellDateValue(row.getCell(3)));
        purchase.setVendor(getCellStringValue(row.getCell(4)));
        purchase.setUnitsOrdered((int) getCellNumericValue(row.getCell(5)));
        purchase.setCostPerUnit(BigDecimal.valueOf(getCellNumericValue(row.getCell(6))));
        purchase.setAisleNumber((int) getCellNumericValue(row.getCell(7)));
        purchase.setBayNumber((int) getCellNumericValue(row.getCell(8)));

        return purchase;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return cell.toString();
    }

    private double getCellNumericValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0.0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value: " + cell.getStringCellValue());
            }
        }
        return 0.0;
    }

    private LocalDate getCellDateValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue(), DATE_FORMATTER);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + cell.getStringCellValue() + ". Use yyyy-MM-dd");
            }
        }
        return null;
    }
}

