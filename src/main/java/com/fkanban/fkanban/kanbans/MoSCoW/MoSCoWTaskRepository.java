package com.fkanban.fkanban.kanbans.MoSCoW;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoSCoWTaskRepository extends JpaRepository<MoSCoWTask, Long> {
    List<MoSCoWTask> findByKanbanId(Long kanbanId);
}
