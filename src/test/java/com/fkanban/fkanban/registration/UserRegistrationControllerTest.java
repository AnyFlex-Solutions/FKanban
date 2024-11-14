package com.fkanban.fkanban.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserRegistrationControllerTest {

    @InjectMocks
    private UserRegistrationController userRegistrationController;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ValidRequest_ShouldReturnToken() {
        RegistrationRequest request = new RegistrationRequest("Test User", "test@example.com", "password123");
        when(registrationService.register(request)).thenReturn("testToken");

        ResponseEntity<Map<String, String>> response = userRegistrationController.register(request, model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testToken", Objects.requireNonNull(response.getBody()).get("token"));
    }

    @Test
    void register_InvalidEmail_ShouldReturnBadRequest() {
        RegistrationRequest request = new RegistrationRequest("Test User", "invalid-email", "password123");
        when(registrationService.register(request)).thenThrow(new IllegalStateException("Email not valid"));

        ResponseEntity<Map<String, String>> response = userRegistrationController.register(request, model);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email not valid", Objects.requireNonNull(response.getBody()).get("error"));
    }
}
