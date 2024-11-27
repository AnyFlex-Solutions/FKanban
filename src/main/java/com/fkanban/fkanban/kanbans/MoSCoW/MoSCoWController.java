package com.fkanban.fkanban.kanbans.MoSCoW;

import com.fkanban.fkanban.kanbans.KanbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/kanban")
public class MoSCoWController {
    @Autowired
    private KanbanService kanbanService;

    // Получение всех задач MoSCoW для конкретной доски Kanban
    @GetMapping("/{kanbanId}/moscow-tasks")
    @ResponseBody
    public List<MoSCoWTask> getAllMoSCoWTaskTasks(@PathVariable Long kanbanId) {
        return kanbanService.getAllMoSCoWTasksByKanbanId(kanbanId);
    }

    // Создание новой задачи MoSCoW для указанной доски Kanban
    @PostMapping("/{kanbanId}/moscow-tasks")
    @ResponseBody
    public MoSCoWTask createMoSCoWTask(@PathVariable Long kanbanId, @RequestBody MoSCoWTask task) {
        return kanbanService.saveMoSCoWTask(kanbanId, task);
    }

    // Обновление задачи MoSCoW по её ID
    @PutMapping("/{kanbanId}/moscow-tasks/{taskId}")
    @ResponseBody
    public MoSCoWTask updateKanoTask(@PathVariable Long taskId, @RequestBody MoSCoWTask taskDetails) {
        return kanbanService.updateMoSCoWTask(taskId, taskDetails);
    }

    // Удаление задачи MoSCoW по её ID
    @DeleteMapping("/moscow-tasks/{taskId}")
    @ResponseBody
    public void deleteMoSCoWTask(@PathVariable Long taskId) {
        kanbanService.deleteMoSCoWTask(taskId);
    }

    // Синхронизация задач MoSCoW на доске Kanban
    @PostMapping("/{kanbanId}/moscow-tasks/sync")
    @ResponseBody
    public ResponseEntity<?> syncMoSCoWTasks(@PathVariable Long kanbanId, @RequestBody List<MoSCoWTask> tasks) {
        kanbanService.syncMoSCoWTasks(kanbanId, tasks);
        return ResponseEntity.ok().body(Collections.singletonMap("status", "success"));
    }
}
