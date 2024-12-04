package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRepository;
import com.fkanban.fkanban.kanbans.Kanban;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Gauge;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final Counter successfulInvitationsCounter;
    private final Counter failedInvitationsCounter;
    final AtomicInteger activeInvitationsCount;
    private final AppUserRepository appUserRepository;

    // Конструктор для внедрения зависимостей
    public InvitationService(InvitationRepository invitationRepository, AppUserRepository appUserRepository, MeterRegistry meterRegistry) {
        this.invitationRepository = invitationRepository;
        this.appUserRepository = appUserRepository;
        this.successfulInvitationsCounter = meterRegistry.counter("invitations.successful");
        this.failedInvitationsCounter = meterRegistry.counter("invitations.failed");

        this.activeInvitationsCount = new AtomicInteger(0);
        Gauge.builder("invitations.active", activeInvitationsCount, AtomicInteger::get)
                .register(meterRegistry);
    }

    // Метод для создания нового приглашения
    public void createInvitation(Kanban kanban, AppUser inviter, AppUser invitee) {
        try {
            Invitation invitation = new Invitation();
            invitation.setKanban(kanban);
            invitation.setInviter(inviter);
            invitation.setInvitee(invitee);
            invitation.setActive(true);
            invitationRepository.save(invitation);
            successfulInvitationsCounter.increment(); // Увеличиваем счётчик успешных приглашений
            activeInvitationsCount.incrementAndGet(); // Увеличиваем счётчик активных приглашений
        } catch (Exception e) {
            failedInvitationsCounter.increment(); // Увеличиваем счётчик неудачных приглашений
            throw new RuntimeException("Failed to create invitation", e);
        }
    }

    // Метод для деактивации приглашения
    public void deactivateInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found"));
        if (invitation.isActive()) {
            invitation.setActive(false);
            invitationRepository.save(invitation);
            activeInvitationsCount.decrementAndGet(); // Уменьшаем счётчик активных приглашений
        }
    }

    // Получаем текущего авторизованного пользователя
    public AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return appUserRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        throw new IllegalStateException("Authentication failed");
    }

    // Сохранение приглашения
    public Invitation save(Invitation invitation) {
        return invitationRepository.save(invitation);
    }

    // Нахождение приглашения по ID
    public Invitation findById(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
    }

    // Деактивация всех приглашений для указанной доски
    public void deactivateInvitationsByKanbanId(Long kanbanId) {
        List<Invitation> invitations = invitationRepository.findByKanbanIdAndIsActive(kanbanId, true);
        for (Invitation invitation : invitations) {
            invitation.setActive(false);
        }
        invitationRepository.saveAll(invitations); // Сохраняем все деактивированные приглашения
    }

    // Метод для формирования email-сообщения с приглашением
    public String buildEmail(String name, String link) {
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
