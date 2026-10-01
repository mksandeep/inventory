package org.sam.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.sam.inventory.entity.Product;
import org.sam.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ExpiryNotificationScheduler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Value("${expiry.notification.email:m.k.sandeep@gmail.com}")
    private String notificationEmail;

    // Run every day at 8 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkExpiringProducts() {
        log.info("Starting expiry notification check...");

        LocalDate today = LocalDate.now();
        LocalDate oneMonthLater = today.plusMonths(1);
        LocalDate threeMonthsLater = today.plusMonths(3);

        try {
            // Check for products expiring in next month (RED alert)
            List<Product> redAlertProducts = productRepository.findAll().stream()
                .filter(p -> p.getExpiryDate() != null &&
                        !p.getExpiryDate().isBefore(today) &&
                        p.getExpiryDate().isBefore(oneMonthLater.plusDays(1)))
                .toList();

            for (Product product : redAlertProducts) {
                emailService.sendExpiryNotification(
                    notificationEmail,
                    product.getProductDescription(),
                    product.getSku(),
                    product.getExpiryDate().toString(),
                    "RED"
                );
                log.info("RED alert sent for product: {}", product.getSku());
            }

            // Check for products expiring in 1-3 months (YELLOW alert)
            List<Product> yellowAlertProducts = productRepository.findAll().stream()
                .filter(p -> p.getExpiryDate() != null &&
                        p.getExpiryDate().isAfter(oneMonthLater) &&
                        !p.getExpiryDate().isAfter(threeMonthsLater))
                .toList();

            for (Product product : yellowAlertProducts) {
                emailService.sendExpiryNotification(
                    notificationEmail,
                    product.getProductDescription(),
                    product.getSku(),
                    product.getExpiryDate().toString(),
                    "YELLOW"
                );
                log.info("YELLOW alert sent for product: {}", product.getSku());
            }

            log.info("Expiry notification check completed. RED alerts: {}, YELLOW alerts: {}",
                redAlertProducts.size(), yellowAlertProducts.size());

        } catch (Exception e) {
            log.error("Error checking expiring products: {}", e.getMessage(), e);
        }
    }
}

