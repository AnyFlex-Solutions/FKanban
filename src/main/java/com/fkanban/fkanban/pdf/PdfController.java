package com.fkanban.fkanban.pdf;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/api/pdf")
public class PdfController {
    @GetMapping("/personal_data_processing_policy")
    public void getPdf1(HttpServletResponse response) throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("static/pdf/personal_data_processing_policy.pdf");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=personal_data_processing_policy.pdf");

        StreamUtils.copy(pdfFile.getInputStream(), response.getOutputStream());
    }

    @GetMapping("/user_agreement")
    public void getPdf2(HttpServletResponse response) throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("static/pdf/user_agreement.pdf");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=user_agreement.pdf");

        StreamUtils.copy(pdfFile.getInputStream(), response.getOutputStream());
    }
}
