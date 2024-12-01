package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserService;
import com.fkanban.fkanban.email.EmailSender;
import com.fkanban.fkanban.kanbans.Kanban;
import com.fkanban.fkanban.kanbans.KanbanService;
import com.fkanban.fkanban.kanbans.invite.token.InvitationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvitationControllerTest {

    @InjectMocks
    private InvitationController invitationController;

    @Mock
    private InvitationService invitationService;

    @Mock
    private KanbanService kanbanService;

    @Mock
    private AppUserService appUserService;

    @Mock
    private InvitationTokenService invitationTokenService;

    @Mock
    private EmailSender emailSender;

    @Mock
    private InvitationRepository invitationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInviteUser_Success() {
        Long kanbanId = 1L;
        String inviteeEmail = "test@example.com";
        AppUser invitee = new AppUser();
        invitee.setEmail(inviteeEmail);
        Kanban kanban = new Kanban();
        AppUser inviter = new AppUser();
        inviter.setEmail("inviter@example.com");

        when(appUserService.checkUserByEmail(inviteeEmail)).thenReturn(true);
        when(appUserService.findUserByEmail(inviteeEmail)).thenReturn(invitee);
        when(kanbanService.findById(kanbanId)).thenReturn(kanban);
        when(invitationService.getCurrentUser()).thenReturn(inviter);
        when(invitationRepository.findByKanbanAndInvitee(kanban, invitee)).thenReturn(Optional.empty());
        when(invitationTokenService.createInvitationToken(kanban, inviter, invitee)).thenReturn("test-token");

        ResponseEntity<Map<String, String>> response = invitationController.inviteUser(kanbanId, inviteeEmail);

        assertNotNull(response);
        assertEquals("Приглашение было успешно отправлено.", response.getBody().get("message"));
        verify(emailSender).send(eq(inviteeEmail), anyString());
    }

    @Test
    void testInviteUser_AlreadyInvited() {
        Long kanbanId = 1L;
        String inviteeEmail = "test@example.com";
        AppUser invitee = new AppUser();
        invitee.setEmail(inviteeEmail);
        Kanban kanban = new Kanban();
        AppUser inviter = new AppUser();

        when(appUserService.checkUserByEmail(inviteeEmail)).thenReturn(true);
        when(appUserService.findUserByEmail(inviteeEmail)).thenReturn(invitee);
        when(kanbanService.findById(kanbanId)).thenReturn(kanban);
        when(invitationService.getCurrentUser()).thenReturn(inviter);

        Invitation existingInvitation = new Invitation();
        existingInvitation.setActive(true);
        when(invitationRepository.findByKanbanAndInvitee(kanban, invitee)).thenReturn(Optional.of(existingInvitation));

        Error exception = assertThrows(Error.class, () -> invitationController.inviteUser(kanbanId, inviteeEmail));
        assertEquals("Пользователь уже имеет доступ", exception.getMessage());
    }

    @Test
    void testInviteUser_UserNotRegistered() {
        Long kanbanId = 1L;
        String inviteeEmail = "unregistered@example.com";

        when(appUserService.checkUserByEmail(inviteeEmail)).thenReturn(false);

        Error exception = assertThrows(Error.class, () -> invitationController.inviteUser(kanbanId, inviteeEmail));
        assertEquals("Пользователь с почтой unregistered@example.com не зарегистрирован", exception.getMessage());
    }
}
