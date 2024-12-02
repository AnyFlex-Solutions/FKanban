package com.fkanban.fkanban.kanbans;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.invite.Invitation;
import com.fkanban.fkanban.kanbans.kano.KanoTask;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Kanban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty") // Проверка, что название не пустое
    private String title;

    // Связь с задачами (Task) в доске Канбан
    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks;

    // Связь с задачами типа Kano (KanoTask)
    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<KanoTask> kano_tasks;

    // Связь с пользователем (AppUser), который создал Канбан
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    // Связь с приглашениями (Invitation), отправленными для Канбан
    @OneToMany(mappedBy = "kanban", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Invitation> invitations;

    private boolean isDeleted = false; // Флаг для логического удаления Канбан

    // Конструктор копирования
    public Kanban(Kanban other) {
        this.id = other.id;
        this.title = other.title;
        this.tasks = new ArrayList<>(other.tasks);
        this.kano_tasks = new ArrayList<>(other.kano_tasks);
        this.user = other.user;
        this.invitations = new ArrayList<>(other.invitations);
        this.isDeleted = other.isDeleted;
    }
}