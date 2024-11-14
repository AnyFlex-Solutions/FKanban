package com.fkanban.fkanban.appuser;

import com.fkanban.fkanban.email.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {
    private AppUserRepository appUserRepository;
    private EmailSender emailSender;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        emailSender = mock(EmailSender.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        passwordResetService = new PasswordResetService(appUserRepository, emailSender, bCryptPasswordEncoder);
    }

    @Test
    void sendResetPasswordEmail_UserExists_ShouldSendEmail() {
        AppUser user = new AppUser("testUser", "test@example.com", "oldPassword", AppUserRole.USER);
        when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");

        passwordResetService.sendResetPasswordEmail("test@example.com");

        verify(appUserRepository).save(user);
        verify(emailSender).send(eq("test@example.com"), anyString());
    }

    @Test
    void sendResetPasswordEmail_UserNotFound_ShouldThrowException() {
        when(appUserRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> passwordResetService.sendResetPasswordEmail("unknown@example.com"));
    }
}
