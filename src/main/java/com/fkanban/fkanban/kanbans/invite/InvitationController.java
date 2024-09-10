package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserService;
import com.fkanban.fkanban.email.EmailSender;
import com.fkanban.fkanban.kanbans.Kanban;
import com.fkanban.fkanban.kanbans.KanbanService;
import com.fkanban.fkanban.kanbans.invite.token.InvitationToken;
import com.fkanban.fkanban.kanbans.invite.token.InvitationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/api/kanban")
public class InvitationController {
    private final InvitationService invitationService;
    private final KanbanService kanbanService;
    private final AppUserService appUserService;
    private final InvitationTokenService invitationTokenService;
    private final InvitationRepository invitationRepository;
    private final EmailSender emailSender;

    // Конструктор для внедрения зависимостей
    public InvitationController(InvitationRepository invitationRepository,
                                AppUserService appUserService,
                                KanbanService kanbanService,
                                InvitationService invitationService,
                                EmailSender emailSender,
                                InvitationTokenService invitationTokenService) {
        this.invitationRepository = invitationRepository;
        this.appUserService = appUserService;
        this.kanbanService = kanbanService;
        this.invitationService = invitationService;
        this.emailSender = emailSender;
        this.invitationTokenService = invitationTokenService;
    }

    @PostMapping("/{kanbanId}/invite")
    public ResponseEntity<Map<String, String>> inviteUser(@PathVariable Long kanbanId, @RequestParam String inviteeEmail) {
        if (appUserService.checkUserByEmail(inviteeEmail)) {
            AppUser invitee = appUserService.findUserByEmail(inviteeEmail);
            Kanban kanban = kanbanService.findById(kanbanId);
            AppUser inviter = invitationService.getCurrentUser();

            if (Objects.equals(inviter.getUsername(), inviteeEmail)) {
                throw new Error("Пользователь уже имеет доступ");
            }

            // Проверка существующего приглашения

            Optional<Invitation> existingInvitation = invitationRepository.findByKanbanAndInvitee(kanban, invitee);
            if (existingInvitation.isPresent()) {
                Invitation invitation = existingInvitation.get();
                if (invitation.isActive()) {
                    throw new Error("Пользователь уже имеет доступ");
                } else {
                    // Если приглашение неактивно, активируем его
                    invitation.setActive(true);
                    invitationService.save(invitation);
                    return ResponseEntity.ok(Map.of("message", "Приглашение было успешно отправлено."));
                }
            } else {
                // Создание нового приглашения
                String token = invitationTokenService.createInvitationToken(kanban, inviter, invitee);
                String link = "http://localhost:8090/api/kanban/invite/confirm?token=" + token;
                emailSender.send(inviteeEmail, buildEmail(invitee.getName(), link));

                Map<String, String> response = new HashMap<>();
                response.put("message", "Приглашение было успешно отправлено.");
                return ResponseEntity.ok(response);
            }
        }

        throw new Error("Пользователь с почтой " + inviteeEmail + " не зарегистрирован");
    }

    @GetMapping("/invite/confirm")
    public String confirmInvitation(@RequestParam("token") String token) {
        invitationTokenService.setConfirmedAt(token);
        InvitationToken invitationToken = invitationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        invitationService.createInvitation(invitationToken.getKanban(), invitationToken.getInviter(), invitationToken.getInvitee());
        return "redirect:/api/kanban/menu";
    }

    @PostMapping("/{kanbanId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateKanban(@PathVariable Long kanbanId) {
        invitationService.deactivateInvitation(kanbanId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Доска успешно деактивирована.");
        return ResponseEntity.ok(response);
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.6; padding: 20px; max-width: 600px; margin: auto; background-color: #f4f4f4;\">\n" +
                "    <div style=\"background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "        <h1 style=\"color: #007bff; font-size: 24px; margin-bottom: 20px;\">Приглашение на FlexiKanban</h1>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Здравствуйте, " + name + "!</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Вы были приглашены присоединиться к доске Kanban на платформе FlexiKanban. Чтобы принять приглашение и присоединиться, пожалуйста, перейдите по следующей ссылке:</p>\n" +
                "        <a href=\"" + link + "\" style=\"display: inline-block; font-size: 16px; color: #ffffff; background-color: #007bff; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-bottom: 20px;\">Присоединиться к доске Kanban</a>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">Если вы не ожидали этого письма, просто проигнорируйте его.</p>\n" +
                "        <p style=\"font-size: 16px; margin-bottom: 20px;\">С наилучшими пожеланиями,<br>Команда FlexiKanban</p>\n" +
                "    </div>\n" +
                "</div>";
    }
}
