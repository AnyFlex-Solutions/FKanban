package com.fkanban.fkanban.kanbans.invite.token;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.Kanban;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// Сервис для работы с токенами приглашений
@Service
@AllArgsConstructor
public class InvitationTokenService {
    private final InvitationTokenRepository invitationTokenRepository;

    // Создание токена приглашения
    public String createInvitationToken(Kanban kanban, AppUser inviter, AppUser invitee) {
        String token = UUID.randomUUID().toString();
        InvitationToken invitationToken = new InvitationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                kanban,
                inviter,
                invitee
        );
        invitationTokenRepository.save(invitationToken);
        return token;
    }

    // Получение токена по значению
    public Optional<InvitationToken> getToken(String token) {
        return invitationTokenRepository.findByToken(token);
    }

    // Установка времени подтверждения токена
    public void setConfirmedAt(String token) {
        InvitationToken invitationToken = getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
        invitationToken.setConfirmedAt(LocalDateTime.now());
        invitationTokenRepository.save(invitationToken);
    }
}
