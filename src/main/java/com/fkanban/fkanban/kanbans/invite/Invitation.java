package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.Kanban;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kanban_id", nullable = false)
    private Kanban kanban;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private AppUser inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_id", nullable = false)
    private AppUser invitee;

    private boolean isActive = true;

    public boolean isActive() {
        return isActive;
    }
}
