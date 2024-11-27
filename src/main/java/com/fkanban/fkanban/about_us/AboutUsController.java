package com.fkanban.fkanban.about_us;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Контроллер, обрабатывающий запросы к API
@Controller
@RequestMapping("/api")
public class AboutUsController {
    // Возвращает страницу "О нас"
    @GetMapping("/about-us")
    public String showLoginPage() {
        return "about_us";
    }

    // Возвращает страницу "Контакты"
    @GetMapping("/kontakts")
    public String showKontaktPage() {
        return "kontakts";
    }
}
