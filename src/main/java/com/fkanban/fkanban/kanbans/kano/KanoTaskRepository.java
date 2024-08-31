package com.fkanban.fkanban.kanbans.kano;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KanoTaskRepository extends JpaRepository<KanoTask, Long> {
    List<KanoTask> findByKanbanId(Long kanbanId);
}
