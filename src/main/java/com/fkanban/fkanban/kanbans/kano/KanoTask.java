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
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private String status;

    @ManyToOne
    @JoinColumn(name = "kanban_id", nullable = false)
    @JsonBackReference
    private Kanban kanban;
}
