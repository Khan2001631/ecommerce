package com.khan.EComm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String name, double amount) {
        if (mailSender == null) {
            log.info("Email service is disabled. Skipped sending email to: {} for amount: {}", toEmail, amount);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Payment successful");
            message.setText("Hi " + name + " Your total amount is: " + amount + ". Thanks for shopping with us!!!");
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
