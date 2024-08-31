package com.fkanban.fkanban.kanbans.invite;

import com.fkanban.fkanban.appuser.AppUser;
import com.fkanban.fkanban.kanbans.Kanban;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByInviteeAndIsActiveTrue(AppUser invitee);

    Optional<Invitation> findByKanbanAndInvitee(Kanban kanban, AppUser invitee);
}