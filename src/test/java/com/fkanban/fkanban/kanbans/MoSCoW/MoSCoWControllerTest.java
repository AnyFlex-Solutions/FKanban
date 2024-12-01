package com.fkanban.fkanban.kanbans.MoSCoW;

import com.fkanban.fkanban.kanbans.KanbanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MoSCoWControllerTest {

    private MoSCoWController moscowController;
    private KanbanService kanbanService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        kanbanService = mock(KanbanService.class);
        moscowController = new MoSCoWController();

        // Используем рефлексию для доступа к private полю и устанавливаем mock-канбан-сервис
        Field kanbanServiceField = MoSCoWController.class.getDeclaredField("kanbanService");
        kanbanServiceField.setAccessible(true);
        kanbanServiceField.set(moscowController, kanbanService);
    }

    @Test
    void testGetAllMoSCoWTaskTasks() {
        Long kanbanId = 1L;
        MoSCoWTask task = new MoSCoWTask();
        task.setId(1L);
        task.setTitle("Test Task");

        when(kanbanService.getAllMoSCoWTasksByKanbanId(kanbanId)).thenReturn(Collections.singletonList(task));

        List<MoSCoWTask> tasks = moscowController.getAllMoSCoWTaskTasks(kanbanId);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
        verify(kanbanService, times(1)).getAllMoSCoWTasksByKanbanId(kanbanId);
    }

    @Test
    void testCreateMoSCoWTask() {
        Long kanbanId = 1L;
        MoSCoWTask task = new MoSCoWTask();
        task.setTitle("New Task");

        when(kanbanService.saveMoSCoWTask(kanbanId, task)).thenReturn(task);

        MoSCoWTask createdTask = moscowController.createMoSCoWTask(kanbanId, task);

        assertNotNull(createdTask);
        assertEquals("New Task", createdTask.getTitle());
        verify(kanbanService, times(1)).saveMoSCoWTask(kanbanId, task);
    }

    @Test
    void testUpdateMoSCoWTask() {
        Long taskId = 1L;
        MoSCoWTask taskDetails = new MoSCoWTask();
        taskDetails.setTitle("Updated Task");

        when(kanbanService.updateMoSCoWTask(taskId, taskDetails)).thenReturn(taskDetails);

        MoSCoWTask updatedTask = moscowController.updateKanoTask(taskId, taskDetails);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        verify(kanbanService, times(1)).updateMoSCoWTask(taskId, taskDetails);
    }

    @Test
    void testDeleteMoSCoWTask() {
        Long taskId = 1L;

        doNothing().when(kanbanService).deleteMoSCoWTask(taskId);

        moscowController.deleteMoSCoWTask(taskId);

        verify(kanbanService, times(1)).deleteMoSCoWTask(taskId);
    }

    @Test
    void testSyncMoSCoWTasks() {
        Long kanbanId = 1L;
        MoSCoWTask task = new MoSCoWTask();
        task.setTitle("Task to Sync");

        doNothing().when(kanbanService).syncMoSCoWTasks(kanbanId, Collections.singletonList(task));

        ResponseEntity<?> response = moscowController.syncMoSCoWTasks(kanbanId, Collections.singletonList(task));

        assertNotNull(response);
        assertEquals("success", ((String) ((java.util.Map) response.getBody()).get("status")));
        verify(kanbanService, times(1)).syncMoSCoWTasks(kanbanId, Collections.singletonList(task));
    }
}
