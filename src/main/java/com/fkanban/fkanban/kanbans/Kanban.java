package com.fkanban.fkanban.kanbans;

import com.fkanban.fkanban.appuser.AppUser;
import jakarta.persistence.*;
import lombok.*;

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

    private String title;

    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
}