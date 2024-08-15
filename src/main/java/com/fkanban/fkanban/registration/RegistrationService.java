package com.fkanban.fkanban.registration;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRole;
import com.fkanban.fkanban.appuser.AppUserService;
import com.fkanban.fkanban.email.EmailSender;
import com.fkanban.fkanban.registration.token.ConfirmationToken;
import com.fkanban.fkanban.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("Email not valid");
        }

        String token = appUserService.signUpUser(new AppUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
        ));

        String link = "http://localhost:8090/api/v1/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(), buildEmail(request.getName(), link));
        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() ->
                new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser((confirmationToken.getAppUser().getEmail()));

        return "redirect:/api/kanban";
    }

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
