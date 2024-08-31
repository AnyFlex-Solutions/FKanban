package com.fkanban.fkanban.kanbans.kano;

import com.fkanban.fkanban.kanbans.KanbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/kanban")
public class KanoController {
    @Autowired
    private KanbanService kanbanService;

    @GetMapping("/{kanbanId}/kano-tasks")
    @ResponseBody
    public List<KanoTask> getAllKanoTasks(@PathVariable Long kanbanId) {
        return kanbanService.getAllKanoTasksByKanbanId(kanbanId);
    }

    @PostMapping("/{kanbanId}/kano-tasks")
    @ResponseBody
    public KanoTask createKanoTask(@PathVariable Long kanbanId, @RequestBody KanoTask task) {
        return kanbanService.saveKanoTask(kanbanId, task);
    }

    @PutMapping("/{kanbanId}/kano-tasks/{taskId}")
    @ResponseBody
    public KanoTask updateKanoTask(@PathVariable Long taskId, @RequestBody KanoTask taskDetails) {
        return kanbanService.updateKanoTask(taskId, taskDetails);
    }

    @DeleteMapping("/kano-tasks/{taskId}")
    @ResponseBody
    public void deleteKanoTask(@PathVariable Long taskId) {
        kanbanService.deleteKanoTask(taskId);
    }

    @PostMapping("/{kanbanId}/kano-tasks/sync")
    @ResponseBody
    public ResponseEntity<?> syncKanoTasks(@PathVariable Long kanbanId, @RequestBody List<KanoTask> tasks) {
        kanbanService.syncKanoTasks(kanbanId, tasks);
        return ResponseEntity.ok().body(Collections.singletonMap("status", "success"));
    }
}
