package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserService;
import com.fkanban.fkanban.kanbans.Kanban;
import com.fkanban.fkanban.kanbans.KanbanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvitationControllerTest {

    @Mock
    private InvitationService invitationService;

    @Mock
    private AppUserService appUserService;

    @Mock
    private KanbanService kanbanService;

    @InjectMocks
    private InvitationController invitationController;

    @Mock
    private InvitationRepository invitationRepository;

    //@InjectMocks
    //private InvitationService invitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void inviteUser_UserAlreadyHasAccess() {
        Kanban kanban = new Kanban();
        AppUser inviter = new AppUser();
        AppUser invitee = new AppUser();
        invitee.setEmail("invitee@example.com");

        when(invitationService.getCurrentUser()).thenReturn(inviter);
        when(appUserService.checkUserByEmail(invitee.getEmail())).thenReturn(true);
        when(kanbanService.findById(1L)).thenReturn(kanban);

        doThrow(new RuntimeException("User already has access"))
                .when(invitationService)
                .createInvitation(kanban, inviter, invitee);

        Exception exception = assertThrows(RuntimeException.class,
                () -> invitationController.inviteUser(1L, invitee.getEmail()));
        assertEquals("User already has access", exception.getMessage());
    }

    @Test
    void inviteUser_Success() {
        Kanban kanban = new Kanban();
        AppUser invitee = new AppUser();
        invitee.setEmail("invitee@example.com");

        when(invitationService.getCurrentUser()).thenReturn(new AppUser());
        when(appUserService.checkUserByEmail(invitee.getEmail())).thenReturn(false);
        when(kanbanService.findById(1L)).thenReturn(kanban);
        doNothing().when(invitationService).createInvitation(any(), any(), any());

        ResponseEntity<Map<String, String>> response = invitationController.inviteUser(1L, invitee.getEmail());

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Приглашение было успешно отправлено.", response.getBody().get("message"));
    }
}
