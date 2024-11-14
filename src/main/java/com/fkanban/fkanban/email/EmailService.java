package com.fkanban.fkanban.email;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    private final Counter successfulEmailsCounter;
    private final Counter failedEmailsCounter;

    @Autowired
    public EmailService(JavaMailSender mailSender, MeterRegistry meterRegistry) {
        this.mailSender = mailSender;
        this.successfulEmailsCounter = Counter.builder("email.send.success")
                .description("Number of successful email sends")
                .register(meterRegistry);
        this.failedEmailsCounter = Counter.builder("email.send.failure")
                .description("Number of failed email sends")
                .register(meterRegistry);
    }

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Подтвердите действие на сайте FlexiKanban");
            helper.setFrom("flexikanban@gmail.com");
            mailSender.send(mimeMessage);

            successfulEmailsCounter.increment();
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email", e);
            failedEmailsCounter.increment();
            throw new IllegalStateException("Failed to send email", e);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to send email due to runtime exception", e);
            failedEmailsCounter.increment();
            throw new IllegalStateException("Failed to send email due to runtime error", e);
        }
    }

}
