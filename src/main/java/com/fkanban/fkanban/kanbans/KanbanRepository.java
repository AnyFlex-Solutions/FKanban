package com.fkanban.fkanban.kanbans;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanRepository extends JpaRepository<Kanban, Long> {
    List<Kanban> findByUserId(Long userId);
}
