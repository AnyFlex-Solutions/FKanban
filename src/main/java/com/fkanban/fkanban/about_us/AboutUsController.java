package com.fkanban.fkanban.about_us;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class AboutUsController {
    @GetMapping("/about-us")
    public String showLoginPage() {
        return "about_us";
    }

    @GetMapping("/kontakts")
    public String showKontaktPage() {
        return "kontakts";
    }
}
