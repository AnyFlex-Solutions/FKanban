package com.fkanban.fkanban.kanbans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;

    @ManyToOne
    @JoinColumn(name = "kanban_id", nullable = false)
    private Kanban kanban;
}
