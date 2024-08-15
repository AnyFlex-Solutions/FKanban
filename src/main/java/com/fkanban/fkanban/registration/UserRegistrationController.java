package com.fkanban.fkanban.registration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class UserRegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
        registrationService.confirmToken(token);

        // Создаем заголовки для перенаправления
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/kanban");

        // Возвращаем ResponseEntity с кодом 302 (Found) и заголовками для перенаправления
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
