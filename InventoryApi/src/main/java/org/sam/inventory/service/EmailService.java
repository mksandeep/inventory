package org.sam.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@inventorysystem.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendExpiryNotification(String to, String productName, String sku, String expiryDate, String alertLevel) {
        String subject;
        String body;

        if ("RED".equals(alertLevel)) {
            subject = "🚨 URGENT: Product Expiring Next Month - " + productName;
            body = String.format(
                "URGENT ALERT!\n\n" +
                "Product: %s\n" +
                "SKU: %s\n" +
                "Expiry Date: %s\n\n" +
                "This product is expiring within the next month. Immediate action required!\n\n" +
                "Please review and take appropriate action.",
                productName, sku, expiryDate
            );
        } else {
            subject = "⚠️ WARNING: Product Expiring in 3 Months - " + productName;
            body = String.format(
                "PRODUCT EXPIRY WARNING\n\n" +
                "Product: %s\n" +
                "SKU: %s\n" +
                "Expiry Date: %s\n\n" +
                "This product will expire within the next 3 months. Please plan accordingly.\n\n" +
                "Recommended Actions:\n" +
                "- Review current inventory levels\n" +
                "- Plan promotional activities\n" +
                "- Update procurement schedules",
                productName, sku, expiryDate
            );
        }

        sendEmail(to, subject, body);
    }
}

