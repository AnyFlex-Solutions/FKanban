package com.fkanban.fkanban.errors;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MyErrorControllerTest {

    @InjectMocks
    private MyErrorController errorController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter errorCounter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(meterRegistry.counter("error.count", "status", "404")).thenReturn(errorCounter);
    }

    @Test
    void handleError_404Error_Returns404Page() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.NOT_FOUND.value());
        assertEquals("errors/404", errorController.handleError(request));
    }

    @Test
    void handleError_500Error_Returns500Page() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        when(errorCounter.count()).thenReturn(1.0);

        assertEquals("errors/500", errorController.handleError(request));
    }
}
