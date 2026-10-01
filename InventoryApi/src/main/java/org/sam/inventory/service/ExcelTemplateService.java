package org.sam.inventory.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ExcelTemplateService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] generateProductTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Description", "SKU", "Dimensions", "Category", "Aisle", "Bay",
                               "Starting Inventory", "Low Stock Threshold", "Unit Cost", "Markup %", "Current Stock", "Expiry Date"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                font.setColor(IndexedColors.WHITE.getIndex());
                style.setFont(font);
                style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(style);
            }

            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("Sample Product");
            sampleRow.createCell(1).setCellValue("SKU-001");
            sampleRow.createCell(2).setCellValue("10x10x10");
            sampleRow.createCell(3).setCellValue("Electronics");
            sampleRow.createCell(4).setCellValue(1);
            sampleRow.createCell(5).setCellValue(1);
            sampleRow.createCell(6).setCellValue(100);
            sampleRow.createCell(7).setCellValue(10);
            sampleRow.createCell(8).setCellValue(50.00);
            sampleRow.createCell(9).setCellValue(20);
            sampleRow.createCell(10).setCellValue(100);
            sampleRow.createCell(11).setCellValue(LocalDate.now().plusMonths(6).format(DATE_FORMATTER));

            // Set column widths
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateSalesTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Invoice No", "Stock ID", "UPC", "Transaction Date", "Ship Date",
                               "Customer Name", "Units Sold", "Purchased Price", "Sale Price",
                               "Sales Commission", "Shipping Cost", "Handling Cost", "Sell Date"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                font.setColor(IndexedColors.WHITE.getIndex());
                style.setFont(font);
                style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(style);
            }

            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("INV-001");
            sampleRow.createCell(1).setCellValue(1);
            sampleRow.createCell(2).setCellValue("UPC-12345");
            sampleRow.createCell(3).setCellValue(LocalDate.now().format(DATE_FORMATTER));
            sampleRow.createCell(4).setCellValue(LocalDate.now().format(DATE_FORMATTER));
            sampleRow.createCell(5).setCellValue("John Doe");
            sampleRow.createCell(6).setCellValue(5);
            sampleRow.createCell(7).setCellValue(50.00);
            sampleRow.createCell(8).setCellValue(75.50);
            sampleRow.createCell(9).setCellValue(5.00);
            sampleRow.createCell(10).setCellValue(10.00);
            sampleRow.createCell(11).setCellValue(2.50);
            sampleRow.createCell(12).setCellValue(LocalDate.now().format(DATE_FORMATTER));

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generatePurchasesTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Purchases");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Order Reference No", "Product ID", "Order Date", "Expected Delivery Date",
                               "Vendor", "Units Ordered", "Cost Per Unit", "Aisle", "Bay"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                font.setColor(IndexedColors.WHITE.getIndex());
                style.setFont(font);
                style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(style);
            }

            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("PO-001");
            sampleRow.createCell(1).setCellValue(1);
            sampleRow.createCell(2).setCellValue(LocalDate.now().format(DATE_FORMATTER));
            sampleRow.createCell(3).setCellValue(LocalDate.now().plusDays(7).format(DATE_FORMATTER));
            sampleRow.createCell(4).setCellValue("Vendor Company");
            sampleRow.createCell(5).setCellValue(50);
            sampleRow.createCell(6).setCellValue(40.00);
            sampleRow.createCell(7).setCellValue(1);
            sampleRow.createCell(8).setCellValue(1);

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    // Alias for backward compatibility: generateStockTemplate -> generateProductTemplate
    public byte[] generateStockTemplate() throws IOException {
        return generateProductTemplate();
    }
}
