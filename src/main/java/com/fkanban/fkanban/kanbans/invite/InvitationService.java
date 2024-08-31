package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRepository;
import com.fkanban.fkanban.kanbans.Kanban;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;

    private AppUserRepository appUserRepository;

    public void createInvitation(Kanban kanban, AppUser inviter, AppUser invitee) {
        Invitation invitation = new Invitation();
        invitation.setKanban(kanban);
        invitation.setInviter(inviter);
        invitation.setInvitee(invitee);
        invitation.setActive(true);
        invitationRepository.save(invitation);
    }

    public void deactivateInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found"));
        invitation.setActive(false);
        invitationRepository.save(invitation);
    }

    public AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return appUserRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        throw new IllegalStateException("Authentication failed");
    }

    public Invitation save(Invitation invitation) {
        return invitationRepository.save(invitation);
    }

    public Invitation findById(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
    }
}
