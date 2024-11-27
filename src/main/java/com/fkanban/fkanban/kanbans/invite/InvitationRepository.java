package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.Kanban;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    // Находит активные приглашения для конкретного пользователя
    List<Invitation> findByInviteeAndIsActiveTrue(AppUser invitee);

    // Находит приглашение для конкретной доски и пользователя
    Optional<Invitation> findByKanbanAndInvitee(Kanban kanban, AppUser invitee);

    // Находит активные приглашения по ID доски
    List<Invitation> findByKanbanIdAndIsActive(Long kanbanId, boolean isActive);
}