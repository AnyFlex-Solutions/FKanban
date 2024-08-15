package com.fkanban.fkanban.kanbans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/kanban")
public class KanbanController {
    @Autowired
    private KanbanService kanbanService;

    @GetMapping("/{kanbanId}")
    public String kanban(@PathVariable Long kanbanId, Model model) {
        model.addAttribute("kanbanId", kanbanId);
        return "kanban";
    }

    @GetMapping("/{kanbanId}/tasks")
    @ResponseBody
    public List<Task> getAllTasks(@PathVariable Long kanbanId) {
        return kanbanService.getAllTasksByKanbanId(kanbanId);
    }

    @PostMapping("/{kanbanId}/tasks")
    @ResponseBody
    public Task createTask(@PathVariable Long kanbanId, @RequestBody Task task) {
        return kanbanService.saveTask(kanbanId, task);
    }

    @PutMapping("/{kanbanId}/tasks/{taskId}")
    @ResponseBody
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        return kanbanService.updateTask(taskId, taskDetails);
    }

    @DeleteMapping("/{kanbanId}/tasks/{taskId}")
    @ResponseBody
    public void deleteTask(@PathVariable Long kanbanId, @PathVariable Long taskId) {
        kanbanService.deleteTask(kanbanId, taskId);
    }

    @PostMapping("/{kanbanId}/tasks/sync")
    @ResponseBody
    public ResponseEntity<?> syncTasks(@PathVariable Long kanbanId, @RequestBody List<Task> tasks) {
        kanbanService.syncTasks(kanbanId, tasks);
        return ResponseEntity.ok().body(Collections.singletonMap("status", "success"));
    }

    @PostMapping("/new")
    @ResponseBody
    public Kanban createKanban(@RequestBody Kanban kanban) {
        return kanbanService.saveKanban(kanban);
    }

    @GetMapping("")
    @ResponseBody
    public List<Kanban> getAllKanbans() {
        return kanbanService.getAllKanbansForCurrentUser();
    }

    @GetMapping("/menu")
    public String showKanbanMenu(Model model) {
        List<Kanban> kanbans = kanbanService.getAllKanbansForCurrentUser();
        model.addAttribute("kanbans", kanbans);
        return "menu";
    }
}
