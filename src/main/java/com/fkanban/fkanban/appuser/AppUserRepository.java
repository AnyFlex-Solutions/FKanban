package com.fkanban.fkanban.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Репозиторий для работы с пользователями приложения
@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Поиск пользователя по email
    Optional<AppUser> findByEmail(String email);

    // Включение учетной записи пользователя по email
    @Transactional
    @Modifying
    @Query("Update AppUser a " + "Set a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);
}
