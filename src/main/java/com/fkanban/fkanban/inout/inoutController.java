package com.fkanban.fkanban.inout;

import com.fkanban.fkanban.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api")
@AllArgsConstructor
public class inoutController {
    private final AppUserService appUserService;

    @GetMapping("/page/login")
    public String showLoginPage() {
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
