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

// Сервисный класс для управления Kanban и задачами
@Service
public class KanbanService {
    // Инжекцию репозиториев для работы с задачами и пользователями
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

    // Метод для получения всех задач для конкретной Kanban-доски
    public List<Task> getAllTasksByKanbanId(Long kanbanId) {
        return taskRepository.findByKanbanId(kanbanId);
    }

    // Метод для получения всех задач типа Kano для конкретной Kanban-доски
    public List<KanoTask> getAllKanoTasksByKanbanId(Long kanbanId) {
        return kanoTaskRepository.findByKanbanId(kanbanId);
    }

    // Метод для получения всех задач типа MoSCoW для конкретной Kanban-доски
    public List<MoSCoWTask> getAllMoSCoWTasksByKanbanId(Long kanbanId) {
        return moSCoWTaskRepository.findByKanbanId(kanbanId);
    }

    // Получение текущего пользователя из контекста безопасности
    public AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return appUserRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        throw new IllegalStateException("Authentication failed");
    }

    // Сохранение нового Kanban
    public Kanban saveKanban(Kanban kanban) {
        AppUser currentUser = getCurrentUser();
        kanban.setUser(currentUser);
        return kanbanRepository.save(kanban);
    }

    // Сохранение новой задачи для указанного Kanban
    public Task saveTask(Long kanbanId, Task task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        task.setId(null);
        task.setKanban(kanban);

        return taskRepository.save(task);
    }

    // Сохранение новой задачи типа Kano для указанного Kanban
    public KanoTask saveKanoTask(Long kanbanId, KanoTask task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
        task.setKanban(kanban);
        return kanoTaskRepository.save(task);
    }

    // Сохранение новой задачи типа MoSCoW для указанного Kanban
    public MoSCoWTask saveMoSCoWTask(Long kanbanId, MoSCoWTask task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
        task.setKanban(kanban);
        return moSCoWTaskRepository.save(task);
    }

    // Обновление задачи типа Task
    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

    // Обновление задачи типа KanoTask
    public KanoTask updateKanoTask(Long taskId, KanoTask taskDetails) {
        KanoTask task = kanoTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return kanoTaskRepository.save(task);
    }

    // Обновление задачи типа MoSCoWTask
    public MoSCoWTask updateMoSCoWTask(Long taskId, MoSCoWTask taskDetails) {
        MoSCoWTask task = moSCoWTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return moSCoWTaskRepository.save(task);
    }

    // Удаление задачи типа Task
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        taskRepository.delete(task);
    }

    // Удаление задачи типа KanoTask
    public void deleteKanoTask(Long taskId) {
        KanoTask task = kanoTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        kanoTaskRepository.delete(task);
    }

    // Удаление задачи типа MoSCoWTask
    public void deleteMoSCoWTask(Long taskId) {
        MoSCoWTask task = moSCoWTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        moSCoWTaskRepository.delete(task);
    }

    // Синхронизация задач для конкретного Kanban (обновление и удаление)
    public void syncTasks(Long kanbanId, List<Task> tasks) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        List<Task> existingTasks = taskRepository.findByKanbanId(kanbanId);

        // Обновление или добавление задач
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

        // Удаление задач, которые больше не передаются
        for (Task existingTask : existingTasks) {
            if (tasks.stream().noneMatch(t -> t.getId().equals(existingTask.getId()))) {
                taskRepository.delete(existingTask);
            }
        }
    }

    // Синхронизация задач типа Kano для конкретного Kanban
    public void syncKanoTasks(Long kanbanId, List<KanoTask> tasks) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        List<KanoTask> existingTasks = kanoTaskRepository.findByKanbanId(kanbanId);

        // Обновление или добавление задач типа Kano
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

        // Удаление задач типа Kano, которые больше не передаются
        for (KanoTask existingTask : existingTasks) {
            if (tasks.stream().noneMatch(t -> t.getId().equals(existingTask.getId()))) {
                kanoTaskRepository.delete(existingTask);
            }
        }
    }

    // Синхронизация задач типа MoSCoW для конкретного Kanban
    public void syncMoSCoWTasks(Long kanbanId, List<MoSCoWTask> tasks) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        List<MoSCoWTask> existingTasks = moSCoWTaskRepository.findByKanbanId(kanbanId);

        // Обновление или добавление задач типа MoSCoW
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

        // Удаление задач типа MoSCoW, которые больше не передаются
        for (MoSCoWTask existingTask : existingTasks) {
            if (tasks.stream().noneMatch(t -> t.getId().equals(existingTask.getId()))) {
                moSCoWTaskRepository.delete(existingTask);
            }
        }
    }

    // Получение всех Kanban для текущего пользователя
    public List<Kanban> getAllKanbansForCurrentUser() {
        AppUser currentUser = getCurrentUser();

        List<Invitation> invitations = invitationRepository.findByInviteeAndIsActiveTrue(currentUser);

        return invitations.stream()
                .map(Invitation::getKanban)
                .filter(kanban -> !kanban.isDeleted()) // только активные доски
                .distinct()
                .toList();
    }

    // Получение Kanban по id
    public Kanban findById(Long kanbanId) {
        return kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
    }

    // Обновление названия Kanban
    public Map<String, String> updateKanbanTitle(Long kanbanId, String newTitle) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));
        kanban.setTitle(newTitle);
        kanbanRepository.save(kanban);

        Map<String, String> response = new HashMap<>();
        response.put("title", newTitle);
        return response;
    }

    // Сохранение Kanban
    public Kanban save(Kanban kanban) {
        return kanbanRepository.save(kanban);
    }

    // Логическое удаление Kanban (деактивация)
    public void deactivateKanban(Long kanbanId) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalStateException("Kanban not found"));

        invitationService.deactivateInvitationsByKanbanId(kanbanId);

        kanban.setDeleted(true); // Логическое удаление
        kanbanRepository.save(kanban); // Сохранение изменения
    }
}
