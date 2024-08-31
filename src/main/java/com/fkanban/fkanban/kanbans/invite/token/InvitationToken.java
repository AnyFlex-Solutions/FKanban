package com.fkanban.fkanban.kanbans.invite.token;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.Kanban;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InvitationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Kanban kanban;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AppUser inviter;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AppUser invitee;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    public InvitationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Kanban kanban, AppUser inviter, AppUser invitee) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.kanban = kanban;
        this.inviter = inviter;
        this.invitee = invitee;
    }
}
