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

    public InvitationService(InvitationRepository invitationRepository, AppUserRepository appUserRepository, MeterRegistry meterRegistry) {
        this.invitationRepository = invitationRepository;
        this.appUserRepository = appUserRepository;
        this.successfulInvitationsCounter = meterRegistry.counter("invitations.successful");
        this.failedInvitationsCounter = meterRegistry.counter("invitations.failed");

        this.activeInvitationsCount = new AtomicInteger(0);
        Gauge.builder("invitations.active", activeInvitationsCount, AtomicInteger::get)
                .register(meterRegistry);
    }

    public void createInvitation(Kanban kanban, AppUser inviter, AppUser invitee) {
        try {
            Invitation invitation = new Invitation();
            invitation.setKanban(kanban);
            invitation.setInviter(inviter);
            invitation.setInvitee(invitee);
            invitation.setActive(true);
            invitationRepository.save(invitation);
            successfulInvitationsCounter.increment();
            activeInvitationsCount.incrementAndGet();
        } catch (Exception e) {
            failedInvitationsCounter.increment();
            throw new RuntimeException("Failed to create invitation", e);
        }
    }

    public void deactivateInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found"));
        if (invitation.isActive()) {
            invitation.setActive(false);
            invitationRepository.save(invitation);
            activeInvitationsCount.decrementAndGet();
        }
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

    public void deactivateInvitationsByKanbanId(Long kanbanId) {
        List<Invitation> invitations = invitationRepository.findByKanbanIdAndIsActive(kanbanId, true);
        for (Invitation invitation : invitations) {
            invitation.setActive(false);
        }
        invitationRepository.saveAll(invitations);
    }
}
