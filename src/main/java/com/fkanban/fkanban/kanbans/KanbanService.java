package com.fkanban.fkanban.kanbans;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRepository;
import com.fkanban.fkanban.kanbans.MoSCoW.MoSCoWTask;
import com.fkanban.fkanban.kanbans.MoSCoW.MoSCoWTaskRepository;
import com.fkanban.fkanban.kanbans.invite.Invitation;
import com.fkanban.fkanban.kanbans.invite.InvitationRepository;
import com.fkanban.fkanban.kanbans.invite.InvitationService;
import com.fkanban.fkanban.kanbans.kano.KanoTask;
import com.fkanban.fkanban.kanbans.kano.KanoTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class KanbanService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KanoTaskRepository kanoTaskRepository;

    @Autowired
    private MoSCoWTaskRepository moSCoWTaskRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private InvitationService invitationService;

    public List<Task> getAllTasksByKanbanId(Long kanbanId) {
        return taskRepository.findByKanbanId(kanbanId);
    }

    public List<KanoTask> getAllKanoTasksByKanbanId(Long kanbanId) {
        return kanoTaskRepository.findByKanbanId(kanbanId);
    }

    public List<MoSCoWTask> getAllMoSCoWTasksByKanbanId(Long kanbanId) {
        return moSCoWTaskRepository.findByKanbanId(kanbanId);
    }

    public AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return appUserRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        throw new IllegalStateException("Authentication failed");
    }

    public Kanban saveKanban(Kanban kanban) {
        AppUser currentUser = getCurrentUser();
        kanban.setUser(currentUser);
        return kanbanRepository.save(kanban);
    }

    public Task saveTask(Long kanbanId, Task task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        task.setId(null);
        task.setKanban(kanban);

        return taskRepository.save(task);
    }

    public KanoTask saveKanoTask(Long kanbanId, KanoTask task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
        task.setKanban(kanban);
        return kanoTaskRepository.save(task);
    }

    public MoSCoWTask saveMoSCoWTask(Long kanbanId, MoSCoWTask task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
        task.setKanban(kanban);
        return moSCoWTaskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

    public KanoTask updateKanoTask(Long taskId, KanoTask taskDetails) {
        KanoTask task = kanoTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return kanoTaskRepository.save(task);
    }

    public MoSCoWTask updateMoSCoWTask(Long taskId, MoSCoWTask taskDetails) {
        MoSCoWTask task = moSCoWTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return moSCoWTaskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        taskRepository.delete(task);
    }

    public void deleteKanoTask(Long taskId) {
        KanoTask task = kanoTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        kanoTaskRepository.delete(task);
    }

    public void deleteMoSCoWTask(Long taskId) {
        MoSCoWTask task = moSCoWTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        moSCoWTaskRepository.delete(task);
    }

    public void syncTasks(Long kanbanId, List<Task> tasks) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        List<Task> existingTasks = taskRepository.findByKanbanId(kanbanId);

        for (Task task : tasks) {
            Optional<Task> existingTask = taskRepository.findById(task.getId());
            if (existingTask.isPresent()) {
                Task updatedTask = existingTask.get();
                updatedTask.setTitle(task.getTitle());
                updatedTask.setDescription(task.getDescription());
                updatedTask.setStatus(task.getStatus());
                taskRepository.save(updatedTask);
            } else {
                task.setKanban(kanban);
                taskRepository.save(task);
            }
        }

        for (Task existingTask : existingTasks) {
            if (tasks.stream().noneMatch(t -> t.getId().equals(existingTask.getId()))) {
                taskRepository.delete(existingTask);
            }
        }
    }

    public void syncKanoTasks(Long kanbanId, List<KanoTask> tasks) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        List<KanoTask> existingTasks = kanoTaskRepository.findByKanbanId(kanbanId);

        for (KanoTask task : tasks) {
            Optional<KanoTask> existingTask = kanoTaskRepository.findById(task.getId());
            if (existingTask.isPresent()) {
                KanoTask updatedTask = existingTask.get();
                updatedTask.setTitle(task.getTitle());
                updatedTask.setDescription(task.getDescription());
                updatedTask.setStatus(task.getStatus());
                kanoTaskRepository.save(updatedTask);
            } else {
                task.setKanban(kanban);
                kanoTaskRepository.save(task);
            }
        }

        for (KanoTask existingTask : existingTasks) {
            if (tasks.stream().noneMatch(t -> t.getId().equals(existingTask.getId()))) {
                kanoTaskRepository.delete(existingTask);
            }
        }
    }

    public void syncMoSCoWTasks(Long kanbanId, List<MoSCoWTask> tasks) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        List<MoSCoWTask> existingTasks = moSCoWTaskRepository.findByKanbanId(kanbanId);

        for (MoSCoWTask task : tasks) {
            Optional<MoSCoWTask> existingTask = moSCoWTaskRepository.findById(task.getId());
            if (existingTask.isPresent()) {
                MoSCoWTask updatedTask = existingTask.get();
                updatedTask.setTitle(task.getTitle());
                updatedTask.setDescription(task.getDescription());
                updatedTask.setStatus(task.getStatus());
                moSCoWTaskRepository.save(updatedTask);
            } else {
                task.setKanban(kanban);
                moSCoWTaskRepository.save(task);
            }
        }

        for (MoSCoWTask existingTask : existingTasks) {
            if (tasks.stream().noneMatch(t -> t.getId().equals(existingTask.getId()))) {
                moSCoWTaskRepository.delete(existingTask);
            }
        }
    }

    public List<Kanban> getAllKanbansForCurrentUser() {
        AppUser currentUser = getCurrentUser();

        List<Invitation> invitations = invitationRepository.findByInviteeAndIsActiveTrue(currentUser);

        return invitations.stream()
                .map(Invitation::getKanban)
                .filter(kanban -> !kanban.isDeleted()) // только активные доски
                .distinct()
                .toList();
    }


    public Kanban findById(Long kanbanId) {
        return kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
    }

    public Map<String, String> updateKanbanTitle(Long kanbanId, String newTitle) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
        kanban.setTitle(newTitle);
        kanbanRepository.save(kanban);

        Map<String, String> response = new HashMap<>();
        response.put("title", newTitle);
        return response;
    }

    public Kanban save(Kanban kanban) {
        return kanbanRepository.save(kanban);
    }

    public void deactivateKanban(Long kanbanId) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        invitationService.deactivateInvitationsByKanbanId(kanbanId);

        kanban.setDeleted(true); // Логическое удаление
        kanbanRepository.save(kanban); // Сохранение изменения
    }
}
