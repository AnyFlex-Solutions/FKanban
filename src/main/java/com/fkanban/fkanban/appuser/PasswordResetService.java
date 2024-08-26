package com.fkanban.fkanban.appuser;

import com.fkanban.fkanban.email.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class PasswordResetService {
    private final AppUserRepository appUserRepository;
    private final EmailSender emailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void sendResetPasswordEmail(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        String newPassword = generateRandomPassword();
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        appUserRepository.save(user);

        String message = buildPasswordResetEmail(user.getName(), newPassword);

        emailSender.send(email, message);
    }

    private String buildPasswordResetEmail(String name, String newPassword) {
        return "<div style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.6; padding: 20px; max-width: 600px; margin: auto; background-color: #f4f4f4;\">\n" +
                "    <div style=\"background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "        <h1 style=\"color: #007bff; font-size: 24px; margin-bottom: 20px;\">Сброс пароля на FlexiKanban</h1>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Здравствуйте, " + name + "!</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Ваш пароль был успешно сброшен. Ваш новый пароль:</p>\n" +
                "        <p style=\"font-size: 18px; font-weight: bold; color: #007bff; margin-bottom: 20px;\">" + newPassword + "</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Для безопасности, мы рекомендуем Вам сменить этот пароль на тот, который Вам будет легко запомнить, сразу после входа в систему.</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Если Вы не запрашивали сброс пароля, пожалуйста, свяжитесь с нашей службой поддержки немедленно. (flexikanban@gmail.com)</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">С наилучшими пожеланиями,<br>Команда FlexiKanban</p>\n" +
                "    </div>\n" +
                "</div>";
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@%#&";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
