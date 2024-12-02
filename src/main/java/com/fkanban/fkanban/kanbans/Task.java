package com.fkanban.fkanban.kanbans;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Сущность для представления задачи
@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Идентификатор задачи

    private String title; // Название задачи
    private String description; // Описание задачи
    private String status; // Статус задачи

    // Связь с Kanban, задача относится к одной доске
    @ManyToOne
    @JoinColumn(name = "kanban_id", nullable = false)
    @JsonBackReference
    private Kanban kanban;
}
