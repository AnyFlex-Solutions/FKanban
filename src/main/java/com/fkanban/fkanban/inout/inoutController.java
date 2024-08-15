package com.fkanban.fkanban.inout;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class inoutController {
    @GetMapping("/page/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/page/registration")
    public String registration() {
        return "registration";
    }

    @GetMapping("page/registration/success")
    public String registrationSuccess() {
        return "registrationSuccess";
    }

}
