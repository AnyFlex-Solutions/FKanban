package com.fkanban.fkanban.kanbans;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Репозиторий для работы с задачами
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Метод для получения всех задач, относящихся к конкретному Kanban
    List<Task> findByKanbanId(Long kanbanId);
}
