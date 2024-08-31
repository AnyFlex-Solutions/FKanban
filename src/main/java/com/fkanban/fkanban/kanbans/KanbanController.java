package com.fkanban.fkanban.kanbans;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.invite.Invitation;
import com.fkanban.fkanban.kanbans.invite.InvitationRepository;
import com.fkanban.fkanban.kanbans.invite.InvitationService;
import com.fkanban.fkanban.kanbans.invite.token.InvitationToken;
import com.fkanban.fkanban.kanbans.invite.token.InvitationTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/kanban")
public class KanbanController {
    @Autowired
    private KanbanService kanbanService;

    @Autowired
    public InvitationService invitationService;

    @Autowired
    public InvitationRepository invitationRepository;

    @Autowired
    public InvitationTokenService invitationTokenService;

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

    @DeleteMapping("/tasks/{taskId}")
    @ResponseBody
    public void deleteTask(@PathVariable Long taskId) {
        kanbanService.deleteTask(taskId);
    }

    @PostMapping("/{kanbanId}/tasks/sync")
    @ResponseBody
    public ResponseEntity<?> syncTasks(@PathVariable Long kanbanId, @RequestBody List<Task> tasks) {
        kanbanService.syncTasks(kanbanId, tasks);
        return ResponseEntity.ok().body(Collections.singletonMap("status", "success"));
    }

    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<?> createKanban(@Valid @RequestBody Kanban kanban, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Title cannot be empty!");
        }

        kanbanService.saveKanban(kanban);

        AppUser invitee = kanbanService.getCurrentUser();
        AppUser inviter = invitee;

        // Проверка существующего приглашения
        Optional<Invitation> existingInvitation = invitationRepository.findByKanbanAndInvitee(kanban, invitee);
        if (existingInvitation.isPresent()) {
            Invitation invitation = existingInvitation.get();
            if (invitation.isActive()) {
                throw new Error("Пользователь уже имеет доступ");
            } else {
                // Если приглашение неактивно, активируем его
                invitation.setActive(true);
                invitationService.save(invitation);
                return ResponseEntity.ok(Map.of("message", "Приглашение было успешно отправлено."));
            }
        } else {
            // Создание нового приглашения
            String token = invitationTokenService.createInvitationToken(kanban, inviter, invitee);

            invitationTokenService.setConfirmedAt(token);
            InvitationToken invitationToken = invitationTokenService.getToken(token)
                    .orElseThrow(() -> new IllegalStateException("Token not found"));

            invitationService.createInvitation(invitationToken.getKanban(), invitationToken.getInviter(), invitationToken.getInvitee());
        }

        return ResponseEntity.ok(kanban);
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
