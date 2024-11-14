package com.fkanban.fkanban.inout;

import com.fkanban.fkanban.appuser.AppUserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api")
public class inoutController {
    private final AppUserService appUserService;

    private final Counter pageViewCounter;
    private final Counter successfulLoginCounter;

    public inoutController(AppUserService appUserService, MeterRegistry meterRegistry) {
        this.appUserService = appUserService;
        this.pageViewCounter = meterRegistry.counter("page.views", "page", "login");
        this.successfulLoginCounter = meterRegistry.counter("login.success", "status", "successful");
    }

    @GetMapping("/page/login")
    public String showLoginPage() {
        pageViewCounter.increment();
        return "login";
    }

    @GetMapping("/page/registration")
    public String registration(Model model) {
        return "registration";
    }

    @GetMapping("page/registration/success")
    public String registrationSuccess() {
        return "registrationSuccess";
    }

    @GetMapping("/private-office")
    public String getPrivateOfficePage(Model model, Principal principal) {
        if (principal != null) {
            successfulLoginCounter.increment();
            String email = principal.getName();
            UserDetails user = appUserService.loadUserByUsername(email);
            model.addAttribute("user", user);
        } else {
            // Если пользователь не аутентифицирован, редирект на страницу логина
            return "redirect:/login";
        }
        return "private_office";
    }
}
