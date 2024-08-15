package com.fkanban.fkanban.kanbans;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByKanbanId(Long kanbanId);
}
