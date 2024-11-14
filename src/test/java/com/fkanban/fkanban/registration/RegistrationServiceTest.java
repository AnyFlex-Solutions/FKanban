package com.fkanban.fkanban.registration;

import com.fkanban.fkanban.appuser.AppUserService;
import com.fkanban.fkanban.email.EmailSender;
import com.fkanban.fkanban.registration.token.ConfirmationToken;
import com.fkanban.fkanban.registration.token.ConfirmationTokenService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRole;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private AppUserService appUserService;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private EmailSender emailSender;

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter successfulRegistrations;

    @Mock
    private Counter failedRegistrations;

    @Mock
    private Counter successfulConfirmations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(meterRegistry.counter("registrations.success")).thenReturn(successfulRegistrations);
        when(meterRegistry.counter("registrations.failure")).thenReturn(failedRegistrations);
        when(meterRegistry.counter("confirmations.success")).thenReturn(successfulConfirmations);

        registrationService = new RegistrationService(
                appUserService,
                confirmationTokenService,
                emailSender,
                emailValidator,
                meterRegistry
        );
    }

    @Test
    void register_ValidRequest_ShouldReturnToken() {
        RegistrationRequest request = new RegistrationRequest("Test User", "test@example.com", "password123");
        when(emailValidator.test(request.getEmail())).thenReturn(true);
        when(appUserService.signUpUser(any())).thenReturn("testToken");

        String token = registrationService.register(request);

        assertEquals("testToken", token);
        verify(successfulRegistrations).increment();
        verify(emailSender).send(eq("test@example.com"), anyString());
    }

    @Test
    void register_InvalidEmail_ShouldThrowException() {
        RegistrationRequest request = new RegistrationRequest("Test User", "invalid-email", "password123");
        when(emailValidator.test(request.getEmail())).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> registrationService.register(request));
        verify(failedRegistrations).increment();
    }

    @Test
    void confirmToken_ValidToken_ShouldEnableUser() {
        AppUser appUser = new AppUser("Test User", "test@example.com", "password123", AppUserRole.USER);
        ConfirmationToken token = new ConfirmationToken("testToken", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), appUser);

        when(confirmationTokenService.getToken("testToken")).thenReturn(Optional.of(token));

        String redirectUrl = registrationService.confirmToken("testToken");

        assertEquals("redirect:/api/page/registration/success", redirectUrl);
        verify(successfulConfirmations).increment();
        verify(appUserService).enableAppUser("test@example.com");
    }

    @Test
    void confirmToken_ExpiredToken_ShouldThrowException() {
        ConfirmationToken token = new ConfirmationToken("testToken", LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(5), null);
        when(confirmationTokenService.getToken("testToken")).thenReturn(Optional.of(token));

        assertThrows(IllegalStateException.class, () -> registrationService.confirmToken("testToken"));
    }
}
