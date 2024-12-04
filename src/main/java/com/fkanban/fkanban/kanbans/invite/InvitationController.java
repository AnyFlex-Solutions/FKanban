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

// Контроллер для работы с приглашениями
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

    // Отправка приглашения пользователю
    @PostMapping("/{kanbanId}/invite")
    public ResponseEntity<Map<String, String>> inviteUser(@PathVariable Long kanbanId, @RequestParam String inviteeEmail) {
        if (appUserService.checkUserByEmail(inviteeEmail)) {
            AppUser invitee = appUserService.findUserByEmail(inviteeEmail);
            Kanban kanban = kanbanService.findById(kanbanId);
            AppUser inviter = invitationService.getCurrentUser();

            // Проверка существующего приглашения

            if (Objects.equals(inviter.getUsername(), inviteeEmail)) {
                throw new Error("Пользователь уже имеет доступ");
            }

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
                emailSender.send(inviteeEmail, invitationService.buildEmail(invitee.getName(), link));

                Map<String, String> response = new HashMap<>();
                response.put("message", "Приглашение было успешно отправлено.");
                return ResponseEntity.ok(response);
            }
        }

        // Если пользователь с указанным email не найден
        throw new Error("Пользователь с почтой " + inviteeEmail + " не зарегистрирован");
    }

    // Метод для подтверждения приглашения через ссылку
    @GetMapping("/invite/confirm")
    public String confirmInvitation(@RequestParam("token") String token) {
        // Устанавливаем время подтверждения токена
        invitationTokenService.setConfirmedAt(token);
        InvitationToken invitationToken = invitationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        // Создаем приглашение на основе токена
        invitationService.createInvitation(invitationToken.getKanban(), invitationToken.getInviter(), invitationToken.getInvitee());
        return "redirect:/api/kanban/menu"; // Перенаправление на меню Kanban
    }

    // Метод для деактивации доски Kanban
    @PostMapping("/{kanbanId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateKanban(@PathVariable Long kanbanId) {
        invitationService.deactivateInvitation(kanbanId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Доска успешно деактивирована.");
        return ResponseEntity.ok(response);
    }
}
