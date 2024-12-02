package com.fkanban.fkanban.kanbans.kano;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fkanban.fkanban.kanbans.Kanban;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class KanoTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор задачи

    @Column(nullable = false)
    private String title; // Заголовок задачи
    private String description; // Описание задачи
    private String status; // Статус задачи

    // Связь с доской Kanban
    @ManyToOne
    @JoinColumn(name = "kanban_id", nullable = false)
    @JsonBackReference // Прекращаем рекурсивную сериализацию JSON
    private Kanban kanban;
}
