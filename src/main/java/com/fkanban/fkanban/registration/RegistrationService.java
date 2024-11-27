package com.fkanban.fkanban.registration;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRole;
import com.fkanban.fkanban.appuser.AppUserService;
import com.fkanban.fkanban.email.EmailSender;
import com.fkanban.fkanban.registration.token.ConfirmationToken;
import com.fkanban.fkanban.registration.token.ConfirmationTokenService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    private final Counter successfulRegistrations;
    private final Counter failedRegistrations;
    private final Counter successfulConfirmations;

    public RegistrationService(AppUserService appUserService, ConfirmationTokenService confirmationTokenService, EmailSender emailSender, EmailValidator emailValidator, MeterRegistry meterRegistry) {
        this.appUserService = appUserService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSender = emailSender;
        this.emailValidator = emailValidator;
        this.successfulRegistrations = meterRegistry.counter("registrations.success");
        this.failedRegistrations = meterRegistry.counter("registrations.failure");
        this.successfulConfirmations = meterRegistry.counter("confirmations.success");
    }

    // Метод регистрации пользователя
    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            failedRegistrations.increment();
            throw new IllegalStateException("Email not valid");
        }

        // Проверяем корректность пароля
        validatePassword(request.getPassword());

        try {
            // Регистрируем нового пользователя и получаем токен
            String token = appUserService.signUpUser(new AppUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    AppUserRole.USER
            ));

            // Генерируем ссылку для подтверждения регистрации
            String link = "http://localhost:8090/api/v1/registration/confirm?token=" + token;

            // Отправляем email с ссылкой для подтверждения
            emailSender.send(request.getEmail(), buildEmail(request.getName(), link));
            successfulRegistrations.increment();
            return token;
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Email already taken")) {
                failedRegistrations.increment();
                throw new IllegalStateException("Почта уже используется");
            }
            throw e;
        }
    }

    // Метод для проверки пароля
    private void validatePassword(String password) {
        // Минимальная длина пароля
        if (password.length() < 5) {
            throw new IllegalStateException("Password must be at least 5 characters long");
        }

        // Регулярное выражение для проверки допустимых символов
        String passwordPattern = "^[A-Za-z0-9!@%#&]+$";
        if (!password.matches(passwordPattern)) {
            throw new IllegalStateException("Password can only contain letters, numbers, and !@%#& characters");
        }
    }

    // Метод для подтверждения токена
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() ->
                new IllegalStateException("Token not found"));

        // Проверка, был ли уже подтвержден этот токен
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        // Проверка, не истек ли срок действия токена
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        // Подтверждаем токен
        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser((confirmationToken.getAppUser().getEmail()));
        successfulConfirmations.increment();

        return "redirect:/api/page/registration/success";
    }

    // Метод для формирования письма с ссылкой на подтверждение регистрации
    private String buildEmail (String name, String link) {
        return "<div style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.6; padding: 20px; max-width: 600px; margin: auto; background-color: #f4f4f4;\">\n" +
                "    <div style=\"background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "        <h1 style=\"color: #007bff; font-size: 24px; margin-bottom: 20px;\">Подтверждение регистрации на FlexiKanban</h1>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Здравствуйте, " + name + "!</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Благодарим Вас за регистрацию на нашем сайте FlexiKanban. Для завершения регистрации и активации вашей учетной записи, пожалуйста, подтвердите ваш адрес электронной почты, перейдя по следующей ссылке:</p>\n" +
                "        <a href=" + link + " style=\"display: inline-block; font-size: 16px; color: #ffffff; background-color: #007bff; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-bottom: 20px;\">Подтвердить адрес электронной почты. Срок действия ссылки истекает через 15 минут.</a>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Если вы не регистрировались на нашем сайте, просто проигнорируйте это письмо. Если у вас возникли какие-либо вопросы, не стесняйтесь обращаться в нашу службу поддержки.</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">С наилучшими пожеланиями,<br>Команда FlexiKanban</p>\n" +
                "    </div>\n" +
                "</div>";
    }
}
