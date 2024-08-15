package com.fkanban.fkanban.kanbans;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.appuser.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class KanbanService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public List<Task> getAllTasksByKanbanId(Long kanbanId) {
        return taskRepository.findByKanbanId(kanbanId);
    }

    private AppUser getCurrentUser() {
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
        task.setKanban(kanban);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Long kanbanId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        taskRepository.delete(task);
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

    public List<Kanban> getAllKanbansForCurrentUser() {
        AppUser currentUser = getCurrentUser();
        return kanbanRepository.findByUserId(currentUser.getId());
    }
}
