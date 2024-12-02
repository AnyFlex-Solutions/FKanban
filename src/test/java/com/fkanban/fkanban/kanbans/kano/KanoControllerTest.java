package com.fkanban.fkanban.kanbans.kano;

import com.fkanban.fkanban.kanbans.KanbanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class KanoControllerTest {

    @InjectMocks
    private KanoController kanoController;

    @Mock
    private KanbanService kanbanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllKanoTasks() {
        Long kanbanId = 1L;
        KanoTask task = new KanoTask();
        task.setId(1L);
        task.setTitle("Test Task");

        when(kanbanService.getAllKanoTasksByKanbanId(kanbanId)).thenReturn(List.of(task));

        List<KanoTask> result = kanoController.getAllKanoTasks(kanbanId);

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(kanbanService, times(1)).getAllKanoTasksByKanbanId(kanbanId);
    }

    @Test
    void testCreateKanoTask() {
        Long kanbanId = 1L;
        KanoTask task = new KanoTask();
        task.setTitle("New Task");

        when(kanbanService.saveKanoTask(kanbanId, task)).thenReturn(task);

        KanoTask result = kanoController.createKanoTask(kanbanId, task);

        assertEquals("New Task", result.getTitle());
        verify(kanbanService, times(1)).saveKanoTask(kanbanId, task);
    }

    @Test
    void testUpdateKanoTask() {
        Long taskId = 1L;
        KanoTask updatedTask = new KanoTask();
        updatedTask.setTitle("Updated Task");

        when(kanbanService.updateKanoTask(taskId, updatedTask)).thenReturn(updatedTask);

        KanoTask result = kanoController.updateKanoTask(taskId, updatedTask);

        assertEquals("Updated Task", result.getTitle());
        verify(kanbanService, times(1)).updateKanoTask(taskId, updatedTask);
    }

    @Test
    void testDeleteKanoTask() {
        Long taskId = 1L;

        doNothing().when(kanbanService).deleteKanoTask(taskId);

        kanoController.deleteKanoTask(taskId);

        verify(kanbanService, times(1)).deleteKanoTask(taskId);
    }

    @Test
    void testSyncKanoTasks() {
        Long kanbanId = 1L;
        KanoTask task = new KanoTask();
        task.setTitle("Task to Sync");

        doNothing().when(kanbanService).syncKanoTasks(kanbanId, Collections.singletonList(task));

        ResponseEntity<?> response = kanoController.syncKanoTasks(kanbanId, Collections.singletonList(task));

        // Приведение тела ответа к Map
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("status"));
        verify(kanbanService, times(1)).syncKanoTasks(kanbanId, Collections.singletonList(task));
    }
}
