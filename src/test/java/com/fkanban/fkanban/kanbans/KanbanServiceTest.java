package com.fkanban.fkanban.kanbans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class KanbanServiceTest {

    @Mock
    private KanbanRepository kanbanRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private KanbanService kanbanService;

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
        task.setKanban(kanban);
    }

    @Test
    void saveKanban_ShouldReturnSavedKanban() {
        when(kanbanRepository.save(kanban)).thenReturn(kanban);

        Kanban savedKanban = kanbanService.save(kanban);

        assertEquals("Test Kanban", savedKanban.getTitle());
        verify(kanbanRepository, times(1)).save(kanban);
    }

    @Test
    void getAllTasksByKanbanId_ShouldReturnTaskList() {
        when(taskRepository.findByKanbanId(1L)).thenReturn(List.of(task));

        List<Task> tasks = kanbanService.getAllTasksByKanbanId(1L);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void updateTask_ShouldUpdateAndReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        when(taskRepository.save(task)).thenReturn(task);

        Task taskDetails = new Task();
        taskDetails.setTitle("Updated Task");
        taskDetails.setDescription("Updated Description");

        Task updatedTask = kanbanService.updateTask(1L, taskDetails);

        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        kanbanService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }
}
