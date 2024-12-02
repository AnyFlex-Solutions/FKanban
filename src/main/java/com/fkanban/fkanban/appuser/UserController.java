package com.fkanban.fkanban.appuser;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

// REST-контроллер для работы с пользователями
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final AppUserService appUserService; // Сервис для управления пользователями
    private final PasswordResetService passwordResetService; // Сервис для сброса пароля

    // Эндпоинт для обновления данных пользователя
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> updateUser(UpdateUserRequest request) {
        try {
            appUserService.updateUser(request);
            return ResponseEntity.ok("Данные успешно обновлены.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Эндпоинт для сброса пароля (авторизованному пользователю)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        passwordResetService.sendResetPasswordEmail(email);
        return ResponseEntity.ok("Новый пароль отправлен на Вашу почту.");
    }

    // Эндпоинт для сброса пароля по email
    @PostMapping("/forgot-password-with-email")
    public ResponseEntity<?> forgotPasswordWithEmail(@RequestParam("email") String email) {
        try {
            passwordResetService.sendResetPasswordEmail(email);
            return ResponseEntity.ok("Новый пароль отправлен на Вашу почту.");
        } catch (IllegalStateException e) {
            // Возвращаем понятный ответ клиенту, если пользователь не найден
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Пользователь не найден. Проверьте правильность введенной почты."));
        } catch (Exception e) {
            // Обрабатываем другие возможные ошибки
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ошибка при отправке нового пароля. Попробуйте позже."));
        }
    }
}
