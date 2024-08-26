package com.fkanban.fkanban.registration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class UserRegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequest request, Model model) {
        Map<String, String> response = new HashMap<>();

        try {
            String token = registrationService.register(request);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
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
