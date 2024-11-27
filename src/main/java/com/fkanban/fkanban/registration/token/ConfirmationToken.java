package com.fkanban.fkanban.registration.token;

import com.fkanban.fkanban.appuser.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {

    // Генератор последовательности для уникальных значений id
    @SequenceGenerator(
            name = "confirmation_token_sequence",
            sequenceName = "confirmation_token_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "confirmation_token_sequence"
    )

    private Long id; // Уникальный идентификатор токена

    @Column(nullable = false)
    private String token; // Токен для подтверждения
    @Column(nullable = false)
    private LocalDateTime createdAt; // Время создания токена
    @Column(nullable = false)
    private LocalDateTime expiresAt; // Время истечения действия токена
    private LocalDateTime confirmedAt; // Время подтверждения токена (может быть null)

    // Связь с пользователем, который создал токен
    @ManyToOne
    @JoinColumn(nullable = false, name = "app_user_id")
    private AppUser appUser;

    // Конструктор для инициализации токена
    public ConfirmationToken(String token,
                             LocalDateTime createdAt,
                             LocalDateTime expiredAt,
                             AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiredAt;
        this.appUser = appUser;
    }
}
