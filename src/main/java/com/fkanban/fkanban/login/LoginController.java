package com.fkanban.fkanban.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Метод для отображения страницы входа
    @GetMapping("/login")
    String login() {
        return "login";
    }
}