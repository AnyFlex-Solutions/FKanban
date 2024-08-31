package com.fkanban.fkanban.kanbans.invite.token;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.Kanban;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
@AllArgsConstructor
public class InvitationTokenService {
    private final InvitationTokenRepository invitationTokenRepository;

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

    public Optional<InvitationToken> getToken(String token) {
        return invitationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        InvitationToken invitationToken = getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
        invitationToken.setConfirmedAt(LocalDateTime.now());
        invitationTokenRepository.save(invitationToken);
    }
}
