package com.fkanban.fkanban.inout;

import com.fkanban.fkanban.appuser.AppUserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class inoutControllerTest {

    @InjectMocks
    private inoutController inoutController;

    @Mock
    private AppUserService appUserService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Counter mockCounter = mock(Counter.class);

        when(meterRegistry.counter("page.views", "page", "login")).thenReturn(mockCounter);
        when(meterRegistry.counter("login.success", "status", "successful")).thenReturn(mockCounter);

        inoutController = new inoutController(appUserService, meterRegistry);
    }

    @Test
    void getPrivateOfficePage_UserAuthenticated_ReturnsPrivateOffice() {
        when(principal.getName()).thenReturn("test@example.com");
        UserDetails mockUser = mock(UserDetails.class);
        when(appUserService.loadUserByUsername("test@example.com")).thenReturn(mockUser);

        String viewName = inoutController.getPrivateOfficePage(model, principal);
        assertEquals("private_office", viewName);
    }

    @Test
    void getPrivateOfficePage_UserNotAuthenticated_RedirectsToLogin() {
        String viewName = inoutController.getPrivateOfficePage(model, null);
        assertEquals("redirect:/login", viewName);
    }
}
