package com.fkanban.fkanban.appuser;

import com.fkanban.fkanban.registration.token.ConfirmationToken;
import com.fkanban.fkanban.registration.token.ConfirmationTokenService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.micrometer.core.instrument.Counter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppUserServiceTest {
    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private AppUserService appUserService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Counter mockCounter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");

        mockCounter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);
    }

    @Test
    public void signUpUser_UserExists_ShouldThrowException() {
        AppUser appUser = new AppUser("testUser", "test@example.com", "password", AppUserRole.USER);
        when(appUserRepository.findByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appUserService.signUpUser(appUser);
        });
        assertEquals("Почта уже используется", exception.getMessage());
    }

    @Test
    public void signUpUser_NewUser_ShouldSaveUserAndGenerateToken() {
        AppUser appUser = new AppUser("testUser", "test@example.com", "password", AppUserRole.USER);
        when(appUserRepository.findByEmail(appUser.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(appUser.getPassword())).thenReturn("encodedPassword");

        String token = appUserService.signUpUser(appUser);

        assertNotNull(token);
        verify(appUserRepository, times(1)).save(appUser);
        verify(confirmationTokenService, times(1)).saveConfirmationToken(any(ConfirmationToken.class));
    }

    @Test
    public void updateUser_ValidRequest_ShouldUpdateUser() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickname("newName");
        request.setPassword("newPassword");
        request.setSuccessPassword("newPassword");

        AppUser user = new AppUser("testUser", "test@example.com", "oldPassword", AppUserRole.USER);
        when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        appUserService.updateUser(request);

        assertEquals("newName", user.getName());
        assertEquals("encodedNewPassword", user.getPassword());

        verify(mockCounter, times(2)).increment();
        verify(appUserRepository, times(1)).save(user);
    }

    @Test
    public void updateUser_PasswordMismatch_ShouldThrowException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("newPassword");
        request.setSuccessPassword("differentPassword");

        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(new AppUser()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appUserService.updateUser(request);
        });
        assertEquals("Пароли не совпадают.", exception.getMessage());
    }
}
