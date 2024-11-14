package com.fkanban.fkanban.pdf;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PdfControllerTest {

    @InjectMocks
    private PdfController pdfController;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    void getPdf1_ServesPersonalDataPdf() throws IOException {
        pdfController.getPdf1(response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=personal_data_processing_policy.pdf");
        verify(response).getOutputStream();
    }
}
