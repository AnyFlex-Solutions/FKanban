package com.fkanban.fkanban.appuser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private AppUserService appUserService;
    private PasswordResetService passwordResetService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        appUserService = mock(AppUserService.class);
        passwordResetService = mock(PasswordResetService.class);
        userController = new UserController(appUserService, passwordResetService);
    }

    @Test
    void updateUser_ValidRequest_ShouldReturnSuccess() {
        UpdateUserRequest request = new UpdateUserRequest("newNickname", "password", "password");

        ResponseEntity<String> response = userController.updateUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Данные успешно обновлены.", response.getBody());
        verify(appUserService).updateUser(request);
    }

    @Test
    void forgotPassword_ValidUser_ShouldSendEmail() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<String> response = userController.forgotPassword();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Новый пароль отправлен на Вашу почту.", response.getBody());
        verify(passwordResetService).sendResetPasswordEmail("test@example.com");
    }

    @Test
    void forgotPasswordWithEmail_UserNotFound_ShouldReturnNotFound() {
        doThrow(new IllegalStateException("Пользователь не найден"))
                .when(passwordResetService).sendResetPasswordEmail("unknown@example.com");

        ResponseEntity<?> response = userController.forgotPasswordWithEmail("unknown@example.com");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Пользователь не найден. Проверьте правильность введенной почты.", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }
}
