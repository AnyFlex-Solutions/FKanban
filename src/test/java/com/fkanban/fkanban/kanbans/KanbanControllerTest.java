package com.fkanban.fkanban.kanbans;

import com.fkanban.fkanban.kanbans.invite.InvitationRepository;
import com.fkanban.fkanban.kanbans.invite.token.InvitationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class KanbanControllerTest {

    @Mock
    private KanbanService kanbanService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private InvitationTokenService invitationTokenService;

    @InjectMocks
    private KanbanController kanbanController;

    private Kanban kanban;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kanban = new Kanban();
        kanban.setId(1L);
        kanban.setTitle("Test Kanban");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
    }

    @Test
    void getAllKanbans_ShouldReturnKanbanList() {
        when(kanbanService.getAllKanbansForCurrentUser()).thenReturn(List.of(kanban));

        List<Kanban> kanbans = kanbanController.getAllKanbans();

        assertEquals(1, kanbans.size());
        assertEquals("Test Kanban", kanbans.get(0).getTitle());
        verify(kanbanService, times(1)).getAllKanbansForCurrentUser();
    }

    @Test
    void createKanban_ShouldReturnBadRequest_WhenBindingResultHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = kanbanController.createKanban(kanban, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        verify(kanbanService, never()).saveKanban(kanban);
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() {
        when(kanbanService.getAllTasksByKanbanId(1L)).thenReturn(List.of(task));

        List<Task> tasks = kanbanController.getAllTasks(1L);

        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
        verify(kanbanService, times(1)).getAllTasksByKanbanId(1L);
    }
}
