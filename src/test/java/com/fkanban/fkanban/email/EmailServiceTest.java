package com.fkanban.fkanban.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private SimpleMeterRegistry meterRegistry;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        emailService = new EmailService(mailSender, meterRegistry);  // Instantiate with mocked mailSender and SimpleMeterRegistry
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendEmailSuccessfully() throws MessagingException {
        emailService.send("test@example.com", "<h1>Test Email</h1>");

        verify(mailSender, times(1)).send(mimeMessage);
        assert meterRegistry.counter("email.send.success").count() == 1;
        assert meterRegistry.counter("email.send.failure").count() == 0;
    }

    @Test
    void sendEmailFailure() {
        doThrow(new RuntimeException("Mock Exception")).when(mailSender).send(mimeMessage);

        assertThrows(IllegalStateException.class, () -> {
            emailService.send("test@example.com", "<h1>Test Email</h1>");
        });

        verify(mailSender, times(1)).send(mimeMessage);

        assert meterRegistry.counter("email.send.success").count() == 0;
        assert meterRegistry.counter("email.send.failure").count() == 1;
    }
}
