package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRepository;
import com.fkanban.fkanban.kanbans.Kanban;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class InvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private AppUserRepository appUserRepository;

    private InvitationService invitationService;

    private SimpleMeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        invitationService = new InvitationService(invitationRepository, appUserRepository, meterRegistry);
    }

    @Test
    void createInvitation_Success() {
        AppUser inviter = new AppUser();
        AppUser invitee = new AppUser();
        Kanban kanban = new Kanban();

        invitationService.createInvitation(kanban, inviter, invitee);

        verify(invitationRepository, times(1)).save(any(Invitation.class));
        assertEquals(1, meterRegistry.counter("invitations.successful").count());
    }

    @Test
    void createInvitation_Failure() {
        AppUser inviter = new AppUser();
        AppUser invitee = new AppUser();
        Kanban kanban = new Kanban();

        doThrow(new RuntimeException()).when(invitationRepository).save(any(Invitation.class));

        assertThrows(RuntimeException.class, () -> invitationService.createInvitation(kanban, inviter, invitee));
        assertEquals(1, meterRegistry.counter("invitations.failed").count());
    }
}
