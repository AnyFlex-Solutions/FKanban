package com.fkanban.fkanban.kanbans;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Интерфейс репозитория для работы с объектами Kanban
@Repository
public interface KanbanRepository extends JpaRepository<Kanban, Long> {
    // Метод для получения всех Kanban, которые не удалены (isDeleted = false)
    List<Kanban> findByIsDeletedFalse();
}
